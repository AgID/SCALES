package net.scales.hub.controllers;

import net.scales.hub.server.NodeRPCConnection;
import net.scales.hub.utils.HttpUtils;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.scales.flows.IssuePurchaseOrderIndirectFlow;
import net.scales.hub.models.ResponseError;
import net.scales.hub.models.ResponseSuccess;
import net.scales.hub.models.Transaction;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.NotAuthorizedException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
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
public class PurchaseOrderController extends BaseController {

	private final static Logger logger = LoggerFactory.getLogger(PurchaseOrderController.class);

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

	public PurchaseOrderController(NodeRPCConnection rpc, KeycloakSecurityContext securityContext) {
		super(rpc, securityContext);
	}

	@PostMapping("/upload-order")
	public Object uploadOrder(@RequestParam("order") MultipartFile orderFile, @RequestParam("nso") MultipartFile nsoFile) {
		try {
			UserRepresentation user = getKeycloak().realm(realm).users().get(securityContext.getToken().getSubject()).toRepresentation();

			byte[] order = orderFile.getBytes();
			byte[] nso = nsoFile.getBytes();

			if (order.length == 0) {
				return new ResponseError(HttpStatus.BAD_REQUEST, "The order file is required");
			}

			if (nso.length == 0) {
				return new ResponseError(HttpStatus.BAD_REQUEST, "The NSO notification file is required");
			}

			Party sdi = proxy.wellKnownPartyFromX500Name(CordaX500Name.parse(sdiName));

			// Starts flow to issue new token
			String result = proxy.startFlowDynamic(IssuePurchaseOrderIndirectFlow.class, hubId, user.getUsername(), order, nso, sdi).getReturnValue().get();

			String errMsg = null;

			// Checks errors
			switch(result) {
				case "ERR001":
					errMsg = "Can't extract metadata from the order file";
					break;

				case "ERR002":
					errMsg = "Can't extract metadata from the NSO notification file";
					break;

				case "ERR003":
					errMsg = "Only supplier or buyer is allowed to mint order";
					break;

				case "ERR004":
					errMsg = "The order file was minted";
					break;
			}

			if (errMsg != null) {
				return new ResponseError(HttpStatus.BAD_REQUEST, errMsg);
			}

			// Splits result into transaction id and order id
			String[] arr = result.split("_", 2);

			// Notifies for the dispatcher after minted order successfully
			notifyToDispatcher(hubId, user.getUsername(), arr[1]);

			return new ResponseSuccess(new Transaction(result));

		} catch (Exception ex) {
			if (ex instanceof NotAuthorizedException) {
				return new ResponseError(HttpStatus.UNAUTHORIZED);
			}

			logger.error("", ex);

			return new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void notifyToDispatcher(String hubId, String endEntityId, String orderId) {
		try {
			// Creates params
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("type", "order"));
			params.add(new BasicNameValuePair("data", orderId));
			params.add(new BasicNameValuePair("hubId", hubId));
			params.add(new BasicNameValuePair("endEntityId", endEntityId));
			params.add(new BasicNameValuePair("vasUrl", vasUrl));

			int status = HttpUtils.post(dispatcherUrl, params, apiKey);

			if (status != 200) {
				logger.info("Can't send notification to the dispatcher: order," + orderId);

			} else {
				logger.info("Sent notification to the dispatcher: " + vasUrl + "," + endEntityId + ",order," + orderId);
			}

		} catch (Exception ex) {
			logger.error("", ex);
		}
	}

}