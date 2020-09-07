package net.scales.hub.controllers;

import net.scales.hub.server.NodeRPCConnection;
import net.scales.hub.utils.HttpUtils;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.scales.flows.ExtractInvoiceMetadataFlow;
import net.scales.flows.IssueInvoiceDirectFlow;
import net.scales.flows.IssueInvoiceIndirectFlow;
import net.scales.hub.models.ResponseError;
import net.scales.hub.models.ResponseSuccess;
import net.scales.hub.models.Transaction;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.ws.rs.NotAuthorizedException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.keycloak.KeycloakSecurityContext;
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
 * Defines your API endpoints here.
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

	@Value("${sdi.name}")
	private String sdiName;

	@Value("${api.key}")
	private String apiKey;

	public InvoiceController(NodeRPCConnection rpc, KeycloakSecurityContext securityContext) {
		super(rpc, securityContext);
	}

	@PostMapping("/upload-invoice")
	public Object uploadInvoice(@RequestParam("invoice") MultipartFile invoiceFile, @RequestParam(name="sdi", required=false) MultipartFile sdiFile) {
		try {
			UserRepresentation user = getKeycloak().realm(realm).users().get(securityContext.getToken().getSubject()).toRepresentation();

			if (invoiceFile.getBytes().length == 0) {
				return new ResponseError(HttpStatus.BAD_REQUEST, "The invoice file is required");
			}

			Party sdi = proxy.wellKnownPartyFromX500Name(CordaX500Name.parse(sdiName));

			String result;

			if (sdiFile == null) {
				LinkedHashMap<String, String> metadata = proxy.startFlowDynamic(ExtractInvoiceMetadataFlow.class, invoiceFile.getBytes()).getReturnValue().get();

				if (metadata == null) {
					return new ResponseError(HttpStatus.BAD_REQUEST, "Can't extract metadata from the invoice file");
				}

				// Gets corda node by user
				Party counterParty = getNodeByUser(metadata.get("BuyerVATIdentifier"));

				// Starts flow to issue new token
				result = proxy.startFlowDynamic(IssueInvoiceDirectFlow.class, hubId, user.getUsername(), invoiceFile.getBytes(), sdi, counterParty).getReturnValue().get();

			} else {
				// Starts flow to issue new token
				result = proxy.startFlowDynamic(IssueInvoiceIndirectFlow.class, hubId, user.getUsername(), invoiceFile.getBytes(), sdiFile.getBytes(), sdi).getReturnValue().get();
			}

			String errMsg = null;

			// Checks errors
			switch(result) {
				case "ERR001":
					errMsg = "Can't extract metadata from the invoice file";
					break;

				case "ERR002":
					errMsg = "Can't extract metadata from the SDI notification file";
					break;

				case "ERR003":
					errMsg = "Only supplier or buyer is allowed to mint invoice";
					break;

				case "ERR004":
					errMsg = "The invoice file was minted";
					break;
			}

			if (errMsg != null) {
				return new ResponseError(HttpStatus.BAD_REQUEST, errMsg);
			}

			// Splits result into transaction id and invoice id
			String[] elements = result.split("_", 2);

			// Notifies for the dispatcher after minted invoice successfully
			notifyToDispatcher(hubId, user.getUsername(), elements[1]);

			return new ResponseSuccess(new Transaction(result));

		} catch (Exception ex) {
			if (ex instanceof NotAuthorizedException) {
				return new ResponseError(HttpStatus.UNAUTHORIZED);
			}

			logger.error("", ex);

			return new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private Party getNodeByUser(String userId) {
		try {
			// Gets user by id
			JSONArray result = new JSONArray(HttpUtils.get(authUrl + "/admin/realms/" + realm + "/users?username=" + userId, securityContext.getTokenString()));

			if (result.length() == 0) {
				return null;
			}

			// Parses response data
			JSONObject user = result.getJSONObject(0);
			JSONObject attributes = new JSONObject(user.getString("attributes"));

			// Gets name of corda node that user belongs
			String partyName = (new JSONArray(attributes.getString("node"))).getString(0);

			return proxy.wellKnownPartyFromX500Name(CordaX500Name.parse(partyName));

		} catch (Exception ex) {
			logger.error("", ex);

			return null;
		}
	}

	private void notifyToDispatcher(String hubId, String endEntityId, String invoiceId) {
		try {
			// Creates params
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("type", "invoice"));
			params.add(new BasicNameValuePair("data", invoiceId));
			params.add(new BasicNameValuePair("hubId", hubId));
			params.add(new BasicNameValuePair("endEntityId", endEntityId));
			params.add(new BasicNameValuePair("vasUrl", vasUrl));

			int status = HttpUtils.post(dispatcherUrl, params, apiKey);

			if (status != 200) {
				logger.info("Can't send notification to the dispatcher: invoice," + invoiceId);

			} else {
				logger.info("Sent notification to the dispatcher: " + vasUrl + "," + endEntityId + ",invoice," + invoiceId);
			}

		} catch (Exception ex) {
			logger.error("", ex);
		}
	}

}