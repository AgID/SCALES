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

    private final String id;

    public GetInvoiceFileByIdFlow(String id) {
        this.id = id;
    }

    @Override
    @Suspendable
    public String call() throws FlowException {
        try {
            InvoiceDatabaseService db = getServiceHub().cordaService(InvoiceDatabaseService.class);

            Blob data = db.getInvoiceFileById(id);

            if (data != null) {
                return FileUtils.convertToString(data.getBinaryStream());
            }

            return null;

        } catch (Exception ex) {
            throw new FlowException(ex.getMessage());
        }
    }

}