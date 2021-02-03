package net.scales.flows;

import co.paralleluniverse.fibers.Suspendable;

import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.StartableByRPC;
import net.scales.flows.services.PurchaseOrderDatabaseService;

@StartableByRPC
public class GetPurchaseOrderByIdFlow extends FlowLogic<Object> {

    private final String id;

    public GetPurchaseOrderByIdFlow(String id) {
        this.id = id;
    }

    @Override
    @Suspendable
    public Object call() throws FlowException {
        try {
            PurchaseOrderDatabaseService db = getServiceHub().cordaService(PurchaseOrderDatabaseService.class);

            return db.getPurchaseOrderById(id);

        } catch (Exception ex) {
            throw new FlowException(ex.getMessage());
        }
    }

}