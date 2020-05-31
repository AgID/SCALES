package net.scales.vas.controllers;

import net.corda.core.messaging.CordaRPCOps;
import net.scales.flows.GetInvoiceListByEndEntityIdFlow;
import net.scales.flows.GetInvoiceByIdFlow;
import net.scales.flows.GetInvoiceFileByIdFlow;
import net.scales.flows.UpdatePaymentInfoFlow;
import net.scales.flows.UpdateInvoiceFlow;
import net.scales.vas.models.Transaction;
import net.scales.vas.models.ErrorResponse;
import net.scales.vas.models.Invoice;
import net.scales.vas.models.ResultResponse;
import net.scales.vas.server.NodeRPCConnection;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Define your API endpoints here.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api")
public class InvoiceController {

	private final static Logger logger = LoggerFactory.getLogger(InvoiceController.class);

	private final CordaRPCOps proxy;

	private final KeycloakSecurityContext securityContext;

	@Value("${keycloak.auth-server-url}")
	private String url;
	
	@Value("${keycloak.realm}")
	private String realm;

	public InvoiceController(NodeRPCConnection rpc, KeycloakSecurityContext securityContext) {
		this.proxy = rpc.proxy;
		this.securityContext = securityContext;
	}

	@GetMapping("/{endEntityId}/invoiceList")
	public Object getInvoiceListByEndEntity(
		@PathVariable("endEntityId") String endEntityId,
		@RequestParam(name="page", defaultValue="1") int page,
		@RequestParam(name="pageSize", defaultValue="10") int pageSize
	) {
		try {
			Keycloak keycloak = getKeycloak();

			UserRepresentation user = keycloak.realm(realm).users().get(endEntityId).toRepresentation();

			List<String> attribute = user.getAttributes().get("vat");

			if (attribute.size() == 0) {
				return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Please register VAT ID for account");
			}

			List<Invoice> invoices = new ArrayList<>();
			List<Object> rows = proxy.startFlowDynamic(GetInvoiceListByEndEntityIdFlow.class, attribute.get(0), page, pageSize).getReturnValue().get();

			for (Object row : rows) {
				List<Object> data = (List<Object>) row;

				invoices.add(new Invoice(
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
					data.get(15).toString(),
					data.get(16).toString(),
					data.get(17).toString(),
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
					data.get(35).toString()
				));
			}

			return new ResultResponse(invoices);

		} catch(Exception ex) {
			return new ErrorResponse(HttpStatus.NOT_FOUND.value(), "EndEntity not found");
		}
	}

	@GetMapping("/invoice/{invoiceId}")
	public Object getInvoiceById(@PathVariable("invoiceId") String invoiceId) {
		Object result;

		try {
			Object row = proxy.startFlowDynamic(GetInvoiceByIdFlow.class, invoiceId).getReturnValue().get();

			if (row == null) {
				result = new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Invoice not found");

			} else {
				List<Object> data = (List<Object>) row;

				result = new ResultResponse(new Invoice(
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
					data.get(15).toString(),
					data.get(16).toString(),
					data.get(17).toString(),
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
					data.get(35).toString()
				));
			}

		} catch(Exception ex) {
			logger.error("", ex);

			result = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return result;
	}

	@GetMapping("/invoice/download/{invoiceId}")
	public Object downloadInvoiceById(@PathVariable("invoiceId") String invoiceId) {
		try {
			String data = proxy.startFlowDynamic(GetInvoiceFileByIdFlow.class, invoiceId).getReturnValue().get();

			if (data.isEmpty()) {
				return new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Invoice not found");
			}

			return ResponseEntity
				.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + invoiceId + ".xml")
				.contentLength(data.getBytes().length)
				.contentType(MediaType.parseMediaType("application/octet-stream"))
				.body(new InputStreamResource(new ByteArrayInputStream(data.getBytes())));

		} catch(Exception ex) {
			logger.error("", ex);

			return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/paymentInfo/{invoiceId}")
	public Object updatePaymentInfo(
		@PathVariable("invoiceId") String invoiceId,
		@RequestParam String vasId,
		@RequestParam String paidAmountToDate,
		@RequestParam String lastPaymentDate,
		@RequestParam String amountDueForPayment
	) {
		try {
			String transactionId = proxy.startFlowDynamic(UpdatePaymentInfoFlow.Initiator.class, invoiceId, vasId, paidAmountToDate, lastPaymentDate, amountDueForPayment).getReturnValue().get();

			if (transactionId.isEmpty()) {
				return new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Invoice not found");
			}

			return new ResultResponse(new Transaction(transactionId));

		} catch(Exception ex) {
			logger.error("", ex);

			return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/invoiceProcessMetadataUpdate/{invoiceId}")
	public Object updateInvoice(
		@PathVariable("invoiceId") String invoiceId,
		@RequestParam String competentAuthorityUniqueId,
		@RequestParam String invoiceCurentState,
		@RequestParam String invoiceCurrentOwner,
		@RequestParam String approvedSubject
	) {
		try {
			String transactionId = proxy.startFlowDynamic(UpdateInvoiceFlow.Initiator.class, invoiceId, competentAuthorityUniqueId, invoiceCurentState, invoiceCurrentOwner, approvedSubject).getReturnValue().get();

			if (transactionId.isEmpty()) {
				return new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Invoice not found");
			}

			return new ResultResponse(new Transaction(transactionId));

		} catch(Exception ex) {
			logger.error("", ex);

			return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private Keycloak getKeycloak() {
        return KeycloakBuilder.builder()
					.serverUrl(url)
					.realm(realm)
					.authorization(securityContext.getTokenString())
					.resteasyClient(new ResteasyClientBuilder().connectionPoolSize(20).build())
					.build();
	}

}