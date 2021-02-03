package net.scales.vas.controllers;

import net.scales.flows.GetPurchaseOrderByIdFlow;
import net.scales.flows.GetPurchaseOrderFileByIdFlow;
import net.scales.flows.GetPurchaseOrderListByEndEntityIdFlow;
import net.scales.flows.GetPurchaseOrderListFlow;
import net.scales.vas.models.ResponseError;
import net.scales.vas.models.PurchaseOrder;
import net.scales.vas.models.ResponseSuccess;
import net.scales.vas.server.NodeRPCConnection;

import org.keycloak.KeycloakSecurityContext;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.NotAuthorizedException;

/**
 * Defines your API endpoints here.
 */
@RestController
public class PurchaseOrderController extends BaseController {

	private final static Logger logger = LoggerFactory.getLogger(PurchaseOrderController.class);

	public PurchaseOrderController(NodeRPCConnection rpc, KeycloakSecurityContext securityContext) {
		super(rpc, securityContext);
	}

	@GetMapping("/{endEntityId}/orderList")
	public Object getOrderListByEndEntity(
		@PathVariable("endEntityId") String endEntityId,
		@RequestParam(name="page", defaultValue="1") int page,
		@RequestParam(name="pageSize", defaultValue="10") int pageSize
	) {
		try {
			// Gets "ee" account
			UserRepresentation user = getKeycloak().realm(realm).users().get(endEntityId).toRepresentation();

			List<PurchaseOrder> orders = new ArrayList<>();

			// Gets order list from corda node
			List<Object> rows = proxy.startFlowDynamic(GetPurchaseOrderListByEndEntityIdFlow.class, user.getUsername(), page, pageSize).getReturnValue().get();

			for (Object row : rows) {
				orders.add(createOrder((List<Object>) row));
			}

			return new ResponseSuccess(orders);

		} catch(Exception ex) {logger.error("", ex);
			if (ex instanceof NotAuthorizedException) {
				return new ResponseError(HttpStatus.UNAUTHORIZED);
			}

			return new ResponseError(HttpStatus.NOT_FOUND, "End Entity is not found");
		}
	}

	@GetMapping("/orderList")
	public Object getOrderList(
		@RequestParam(name="sellerVatId", defaultValue="") String sellerVatId,
		@RequestParam(name="buyerVatId", defaultValue="") String buyerVatId,
		@RequestParam(name="page", defaultValue="1") int page,
		@RequestParam(name="pageSize", defaultValue="10") int pageSize
	) {
		try {
			if (sellerVatId.isEmpty() && buyerVatId.isEmpty()) {
				return new ResponseError(HttpStatus.BAD_REQUEST, "sellerVatId or buyerVatId is required");
			}

			List<PurchaseOrder> orders = new ArrayList<>();

			// Gets order list from corda node
			List<Object> rows = proxy.startFlowDynamic(GetPurchaseOrderListFlow.class, sellerVatId, buyerVatId, page, pageSize).getReturnValue().get();

			for (Object row : rows) {
				orders.add(createOrder((List<Object>) row));
			}

			return new ResponseSuccess(orders);

		} catch(Exception ex) {
			logger.error("", ex);

			return new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/order/{orderId}")
	public Object getOrderById(@PathVariable("orderId") String orderId) {
		try {
			// Gets order from corda node
			Object row = proxy.startFlowDynamic(GetPurchaseOrderByIdFlow.class, orderId).getReturnValue().get();

			if (row == null) {
				return new ResponseError(HttpStatus.NOT_FOUND, "Order is not found");
			}

			return new ResponseSuccess(createOrder((List<Object>) row));

		} catch(Exception ex) {
			logger.error("", ex);

			return new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/order/download/{orderId}")
	public Object downloadOrderById(@PathVariable("orderId") String orderId) {
		try {
			// Get order file from corda node
			String data = proxy.startFlowDynamic(GetPurchaseOrderFileByIdFlow.class, orderId).getReturnValue().get();

			if (data == null) {
				return new ResponseError(HttpStatus.NOT_FOUND, "Order is not found");
			}

			return ResponseEntity
				.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + orderId + ".xml")
				.contentLength(data.getBytes().length)
				.contentType(MediaType.parseMediaType("application/octet-stream"))
				.body(new InputStreamResource(new ByteArrayInputStream(data.getBytes())));

		} catch(Exception ex) {
			logger.error("", ex);

			return new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private PurchaseOrder createOrder(List<Object> data) {
		return new PurchaseOrder(
			data.get(0).toString(),
			data.get(1).toString(),
			data.get(2).toString(),
			data.get(3).toString(),
			data.get(4).toString(),
			data.get(5).toString(),
			data.get(6).toString(),
			data.get(7).toString(),
			data.get(8).toString(),
			data.get(9).toString(),
			data.get(10).toString(),
			data.get(11).toString(),
			data.get(12).toString(),
			data.get(13).toString(),
			data.get(14).toString(),
			data.get(15) != null ? data.get(15).toString() : null,
			data.get(16) != null ? data.get(16).toString() : null,
			data.get(17) != null ? data.get(17).toString() : null,
			data.get(18).toString(),
			data.get(19).toString(),
			data.get(20).toString(),
			data.get(21).toString(),
			data.get(22).toString(),
			data.get(23).toString(),
			data.get(24).toString(),
			data.get(25).toString(),
			data.get(26).toString(),
			data.get(27).toString(),
			data.get(28).toString(),
			data.get(29).toString(),
			data.get(30).toString(),
			data.get(31).toString(),
			data.get(32).toString(),
			data.get(33).toString(),
			data.get(34).toString(),
			data.get(35).toString(),
			data.get(36).toString(),
			data.get(37).toString()
		);
	}

}