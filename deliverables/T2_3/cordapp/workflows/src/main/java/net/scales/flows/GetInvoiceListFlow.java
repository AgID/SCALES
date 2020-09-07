package net.scales.flows;

import java.util.List;

import co.paralleluniverse.fibers.Suspendable;

import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.StartableByRPC;
import net.scales.flows.services.InvoiceDatabaseService;

@StartableByRPC
public class GetInvoiceListFlow extends FlowLogic<List<Object>> {

    private final String invoiceNumber;
    private final String invoiceTypeCode;
    private final String invoiceIssueDate;
    private final String sellerVatId;
    private final String buyerVatId;
    private final int page;
    private final int pageSize;

    public GetInvoiceListFlow(
        String invoiceNumber,
        String invoiceTypeCode,
        String invoiceIssueDate,
        String sellerVatId,
        String buyerVatId,
        int page,
        int pageSize
    ) {
        this.invoiceNumber = invoiceNumber;
        this.invoiceTypeCode = invoiceTypeCode;
        this.invoiceIssueDate = invoiceIssueDate;
        this.sellerVatId = sellerVatId;
        this.buyerVatId = buyerVatId;
        this.page = page;
        this.pageSize = pageSize;
    }

    @Override
    @Suspendable
    public List<Object> call() throws FlowException {
        try {
            InvoiceDatabaseService db = getServiceHub().cordaService(InvoiceDatabaseService.class);

            return db.getInvoiceList(invoiceNumber, invoiceTypeCode, invoiceIssueDate, sellerVatId, buyerVatId, page, pageSize);

        } catch (Exception ex) {
            throw new FlowException(ex.getMessage());
        }
    }

}