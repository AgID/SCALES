package net.scales.flows;

import java.sql.Blob;

import co.paralleluniverse.fibers.Suspendable;

import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.StartableByRPC;
import net.scales.flows.services.InvoiceDatabaseService;
import net.scales.flows.utils.FileUtils;

@StartableByRPC
public class GetInvoiceFileByIdFlow extends FlowLogic<String> {

    private final String invoiceNumber;

    public GetInvoiceFileByIdFlow(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    @Override
    @Suspendable
    public String call() throws FlowException {
        try {
            InvoiceDatabaseService db = getServiceHub().cordaService(InvoiceDatabaseService.class);

            Blob data = db.getInvoiceFileById(invoiceNumber);

            if (data == null) {
                return "";
            }

            return FileUtils.convertToString(data.getBinaryStream());

        } catch (Exception ex) {
            throw new FlowException(ex.getMessage());
        }
    }

}