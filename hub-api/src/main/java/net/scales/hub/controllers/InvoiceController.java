package net.scales.hub.controllers;

import net.scales.hub.server.NodeRPCConnection;
import net.scales.flows.IssueInvoiceFlow;
import net.scales.hub.models.ErrorResponse;
import net.scales.hub.models.ResultResponse;
import net.scales.hub.models.Transaction;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.NotAuthorizedException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

/**
 * Define your API endpoints here.
 */
@RestController
public class InvoiceController extends BaseController {

	private final static Logger logger = LoggerFactory.getLogger(InvoiceController.class);

	@Value("${hub.id}")
	private String hubId;

	@Value("${vas.url}")
	private String vasUrl;

	@Value("${dispatcher.url}")
	private String dispatcherUrl;

	public InvoiceController(NodeRPCConnection rpc, KeycloakSecurityContext securityContext) {
		super(rpc, securityContext);
	}

	@PostMapping("/upload-invoice")
	public Object uploadInvoice(@RequestParam("invoice") MultipartFile invoiceFile, @RequestParam("sdi") MultipartFile sdiFile) {
		try {
			AccessToken token = getAccessToken();

			Keycloak keycloak = getKeycloak();

			UserRepresentation user = keycloak.realm(getRealm()).users().get(token.getSubject()).toRepresentation();

			// Get VAT id of end entity
			List<String> attribute = user.getAttributes().get("vat");

			// Check that VAT id exists
			if (attribute.size() == 0) {
				return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Please register VAT ID for account");
			}

			byte[] invoice = invoiceFile.getBytes();
			byte[] sdi = sdiFile.getBytes();

			if (invoice.length == 0) {
				return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "The invoice file is required");
			}

			if (sdi.length == 0) {
				return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "The SDI notification file is required");
			}

			// Start flow to issue new token
			String result = proxy.startFlowDynamic(IssueInvoiceFlow.class, hubId, attribute.get(0), invoice, sdi).getReturnValue().get();

			String errMsg = null;

			// Check errors
			switch(result) {
				case "0":
					errMsg = "The invoice file was minted";
					break;

				case "1":
					errMsg = "Can not extract metadata from the invoice file";
					break;

				case "2":
					errMsg = "Can not extract metadata from the SDI notification file";
					break;

				case "3":
					errMsg = "End entity uploaded invalid invoice";
					break;
			}

			if (errMsg != null) {
				return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errMsg);
			}

			// Split result into transaction id and invoice number
			String[] arr = result.split("_", 2);

			// Notify for the dispatcher after minted invoice successfully
			notifyToDispatcher(attribute.get(0), arr[1]);

			return new ResultResponse(new Transaction(arr[0]));

		} catch (Exception ex) {
			if (ex instanceof NotAuthorizedException) {
				return new ErrorResponse(HttpStatus.UNAUTHORIZED);
			}

			logger.error("", ex);

			return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void notifyToDispatcher(String endEntityId, String invoiceNumber) throws Exception {
		// Create params
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("data", vasUrl + "," + endEntityId + "," + invoiceNumber));

		HttpPost request = new HttpPost(dispatcherUrl);
		request.setEntity(new UrlEncodedFormEntity(params));

		// Make request
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpResponse response = httpClient.execute(request);

		if (response.getStatusLine().getStatusCode() != 200) {
			logger.error("", "Can not notify to the dispatcher for invoice " + invoiceNumber);
		}
	}

}