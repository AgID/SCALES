package net.scales.vas.controllers;

import net.scales.flows.GetInvoiceListFlow;
import net.scales.flows.GetInvoiceListByEndEntityIdFlow;
import net.scales.flows.GetInvoiceByIdFlow;
import net.scales.flows.GetInvoiceFileByIdFlow;
import net.scales.flows.UpdatePaymentInfoFlow;
import net.scales.flows.UpdateInvoiceFlow;
import net.scales.vas.models.Transaction;
import net.scales.vas.models.ResponseError;
import net.scales.vas.models.Invoice;
import net.scales.vas.models.ResponseSuccess;
import net.scales.vas.server.NodeRPCConnection;

import org.apache.commons.lang3.ArrayUtils;
import org.keycloak.KeycloakSecurityContext;
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
import org.springframework.web.bind.annotation.PostMapping;
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
public class InvoiceController extends BaseController {

	private final static Logger logger = LoggerFactory.getLogger(InvoiceController.class);

	public InvoiceController(NodeRPCConnection rpc, KeycloakSecurityContext securityContext) {
		super(rpc, securityContext);
	}

	@GetMapping("/{endEntityId}/invoiceList")
	public Object getInvoiceListByEndEntity(
		@PathVariable("endEntityId") String endEntityId,
		@RequestParam(name="page", defaultValue="1") int page,
		@RequestParam(name="pageSize", defaultValue="10") int pageSize
	) {
		try {
			// Gets "ee" account
			UserRepresentation user = getKeycloak().realm(realm).users().get(endEntityId).toRepresentation();

			List<Invoice> invoices = new ArrayList<>();

			// Gets invoice list from corda node
			List<Object> rows = proxy.startFlowDynamic(GetInvoiceListByEndEntityIdFlow.class, user.getUsername(), page, pageSize).getReturnValue().get();

			for (Object row : rows) {
				invoices.add(createInvoice((List<Object>) row));
			}

			return new ResponseSuccess(invoices);

		} catch(Exception ex) {
			if (ex instanceof NotAuthorizedException) {
				return new ResponseError(HttpStatus.UNAUTHORIZED);
			}

			return new ResponseError(HttpStatus.NOT_FOUND, "End Entity is not found");
		}
	}

	@GetMapping("/invoiceList")
	public Object getInvoiceList(
		@RequestParam(name="invoiceNumber", defaultValue="") String invoiceNumber,
		@RequestParam(name="invoiceTypeCode", defaultValue="") String invoiceTypeCode,
		@RequestParam(name="invoiceIssueDate", defaultValue="") String invoiceIssueDate,
		@RequestParam(name="sellerVatId", defaultValue="") String sellerVatId,
		@RequestParam(name="buyerVatId", defaultValue="") String buyerVatId,
		@RequestParam(name="page", defaultValue="1") int page,
		@RequestParam(name="pageSize", defaultValue="10") int pageSize
	) {
		try {
			if (sellerVatId.isEmpty() && buyerVatId.isEmpty()) {
				return new ResponseError(HttpStatus.BAD_REQUEST, "sellerVatId or buyerVatId is required");
			}

			List<Invoice> invoices = new ArrayList<>();

			// Gets invoice list from corda node
			List<Object> rows = proxy.startFlowDynamic(GetInvoiceListFlow.class, invoiceNumber, invoiceTypeCode, invoiceIssueDate, sellerVatId, buyerVatId, page, pageSize).getReturnValue().get();

			for (Object row : rows) {
				invoices.add(createInvoice((List<Object>) row));
			}

			return new ResponseSuccess(invoices);

		} catch(Exception ex) {
			logger.error("", ex);

			return new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/invoice/{invoiceId}")
	public Object getInvoiceById(@PathVariable("invoiceId") String invoiceId) {
		try {
			// Gets invoice from corda node
			Object row = proxy.startFlowDynamic(GetInvoiceByIdFlow.class, invoiceId).getReturnValue().get();

			if (row == null) {
				return new ResponseError(HttpStatus.NOT_FOUND, "Invoice is not found");
			}

			return new ResponseSuccess(createInvoice((List<Object>) row));

		} catch(Exception ex) {
			logger.error("", ex);

			return new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/invoice/download/{invoiceId}")
	public Object downloadInvoiceById(@PathVariable("invoiceId") String invoiceId) {
		try {
			// Gets invoice file from corda node
			String data = proxy.startFlowDynamic(GetInvoiceFileByIdFlow.class, invoiceId).getReturnValue().get();

			if (data == null) {
				return new ResponseError(HttpStatus.NOT_FOUND, "Invoice is not found");
			}

			return ResponseEntity
				.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + invoiceId + ".xml")
				.contentLength(data.getBytes().length)
				.contentType(MediaType.parseMediaType("application/octet-stream"))
				.body(new InputStreamResource(new ByteArrayInputStream(data.getBytes())));

		} catch(Exception ex) {
			logger.error("", ex);

			return new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/paymentInfo/{invoiceId}")
	public Object updatePaymentInfo(
		@PathVariable("invoiceId") String invoiceId,
		@RequestParam String vasId,
		@RequestParam String paidAmountToDate,
		@RequestParam String lastPaymentDate,
		@RequestParam String amountDueForPayment,
		@RequestParam String lastUpdate,
		@RequestParam String paid
	) {
		try {
			if (!ArrayUtils.contains(new String[] { "paid", "unpaid", "partially_paid" }, paid)) {
				return new ResponseError(HttpStatus.BAD_REQUEST, "Parameter 'paid' must be values: paid, unpaid, partially_paid");
			}

			// Updates payment information to corda node
			String transactionId = proxy.startFlowDynamic(UpdatePaymentInfoFlow.Initiator.class, invoiceId, vasId, paidAmountToDate, lastPaymentDate, amountDueForPayment, lastUpdate, paid).getReturnValue().get();

			if (transactionId == null) {
				return new ResponseError(HttpStatus.NOT_FOUND, "Invoice is not found");
			}

			return new ResponseSuccess(new Transaction(transactionId));

		} catch(Exception ex) {
			logger.error("", ex);

			return new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR);
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
			// Updates invoice information to corda node
			String transactionId = proxy.startFlowDynamic(UpdateInvoiceFlow.Initiator.class, invoiceId, competentAuthorityUniqueId, invoiceCurentState, invoiceCurrentOwner, approvedSubject).getReturnValue().get();

			if (transactionId == null) {
				return new ResponseError(HttpStatus.NOT_FOUND, "Invoice is not found");
			}

			return new ResponseSuccess(new Transaction(transactionId));

		} catch(Exception ex) {
			logger.error("", ex);

			return new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private Invoice createInvoice(List<Object> data) {
		return new Invoice(
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
			data.get(35).toString(),
			data.get(36).toString(),
			data.get(37).toString()
		);
	}

}