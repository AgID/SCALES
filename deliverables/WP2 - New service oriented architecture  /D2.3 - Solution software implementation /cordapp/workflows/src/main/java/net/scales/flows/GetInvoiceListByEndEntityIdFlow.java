package net.scales.flows;

import java.util.List;

import co.paralleluniverse.fibers.Suspendable;

import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.StartableByRPC;
import net.scales.flows.services.InvoiceDatabaseService;

@StartableByRPC
public class GetInvoiceListByEndEntityIdFlow extends FlowLogic<List<Object>> {

    private final String endEntityId;
    private final int page;
    private final int pageSize;

    public GetInvoiceListByEndEntityIdFlow(String endEntityId, int page, int pageSize) {
        this.endEntityId = endEntityId;
        this.page = page;
        this.pageSize = pageSize;
    }

    @Override
    @Suspendable
    public List<Object> call() throws FlowException {
        try {
            InvoiceDatabaseService db = getServiceHub().cordaService(InvoiceDatabaseService.class);

            return db.getInvoiceListByEndEntityId(endEntityId, page, pageSize);

        } catch (Exception ex) {
            throw new FlowException(ex.getMessage());
        }
    }

}