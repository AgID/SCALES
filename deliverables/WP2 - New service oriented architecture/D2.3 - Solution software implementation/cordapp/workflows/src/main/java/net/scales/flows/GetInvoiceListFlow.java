package net.scales.flows;

import java.util.LinkedHashMap;

import co.paralleluniverse.fibers.Suspendable;

import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.StartableByRPC;
import net.scales.flows.services.InvoiceDatabaseService;

@StartableByRPC
public class GetInvoiceListFlow extends FlowLogic<LinkedHashMap<String, Object>> {

    private final String invoiceNumber;
    private final String invoiceTypeCode;
    private final String invoiceIssueDateFrom;
    private final String invoiceIssueDateTo;
    private final String sellerVatId;
    private final String buyerVatId;
    private final int page;
    private final int pageSize;

    public GetInvoiceListFlow(
        String invoiceNumber,
        String invoiceTypeCode,
        String invoiceIssueDateFrom,
        String invoiceIssueDateTo,
        String sellerVatId,
        String buyerVatId,
        int page,
        int pageSize
    ) {
        this.invoiceNumber = invoiceNumber;
        this.invoiceTypeCode = invoiceTypeCode;
        this.invoiceIssueDateFrom = invoiceIssueDateFrom;
        this.invoiceIssueDateTo = invoiceIssueDateTo;
        this.sellerVatId = sellerVatId;
        this.buyerVatId = buyerVatId;
        this.page = page;
        this.pageSize = pageSize;
    }

    @Override
    @Suspendable
    public LinkedHashMap<String, Object> call() throws FlowException {
        try {
            InvoiceDatabaseService db = getServiceHub().cordaService(InvoiceDatabaseService.class);

            LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();

            params.put("total", db.countInvoiceList(invoiceNumber, invoiceTypeCode, invoiceIssueDateFrom, invoiceIssueDateTo, sellerVatId, buyerVatId));
            params.put("records", db.getInvoiceList(invoiceNumber, invoiceTypeCode, invoiceIssueDateFrom, invoiceIssueDateTo, sellerVatId, buyerVatId, page, pageSize));

            return params;

        } catch (Exception ex) {
            throw new FlowException(ex.getMessage());
        }
    }

}