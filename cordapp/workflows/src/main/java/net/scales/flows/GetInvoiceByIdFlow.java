package net.scales.flows;

import co.paralleluniverse.fibers.Suspendable;

import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.StartableByRPC;
import net.scales.flows.services.InvoiceDatabaseService;

@StartableByRPC
public class GetInvoiceByIdFlow extends FlowLogic<Object> {

    private final String invoiceNumber;

    public GetInvoiceByIdFlow(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    @Override
    @Suspendable
    public Object call() throws FlowException {
        try {
            InvoiceDatabaseService db = getServiceHub().cordaService(InvoiceDatabaseService.class);

            return db.getInvoiceById(invoiceNumber);

        } catch (Exception ex) {
            throw new FlowException(ex.getMessage());
        }
    }

}