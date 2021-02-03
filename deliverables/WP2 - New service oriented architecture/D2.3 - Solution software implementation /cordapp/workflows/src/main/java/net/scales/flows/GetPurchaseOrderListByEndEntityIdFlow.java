package net.scales.flows;

import java.util.List;

import co.paralleluniverse.fibers.Suspendable;

import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.StartableByRPC;
import net.scales.flows.services.PurchaseOrderDatabaseService;

@StartableByRPC
public class GetPurchaseOrderListByEndEntityIdFlow extends FlowLogic<List<Object>> {

    private final String endEntityId;
    private final int page;
    private final int pageSize;

    public GetPurchaseOrderListByEndEntityIdFlow(String endEntityId, int page, int pageSize) {
        this.endEntityId = endEntityId;
        this.page = page;
        this.pageSize = pageSize;
    }

    @Override
    @Suspendable
    public List<Object> call() throws FlowException {
        try {
            PurchaseOrderDatabaseService db = getServiceHub().cordaService(PurchaseOrderDatabaseService.class);

            return db.getPurchaseOrderListByEndEntityId(endEntityId, page, pageSize);

        } catch (Exception ex) {
            throw new FlowException(ex.getMessage());
        }
    }

}