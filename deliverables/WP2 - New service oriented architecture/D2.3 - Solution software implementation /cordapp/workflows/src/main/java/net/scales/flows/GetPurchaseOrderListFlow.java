package net.scales.flows;

import java.util.List;

import co.paralleluniverse.fibers.Suspendable;

import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.StartableByRPC;
import net.scales.flows.services.PurchaseOrderDatabaseService;

@StartableByRPC
public class GetPurchaseOrderListFlow extends FlowLogic<List<Object>> {

    private final String sellerVatId;
    private final String buyerVatId;
    private final int page;
    private final int pageSize;

    public GetPurchaseOrderListFlow(
        String sellerVatId,
        String buyerVatId,
        int page,
        int pageSize
    ) {
        this.sellerVatId = sellerVatId;
        this.buyerVatId = buyerVatId;
        this.page = page;
        this.pageSize = pageSize;
    }

    @Override
    @Suspendable
    public List<Object> call() throws FlowException {
        try {
            PurchaseOrderDatabaseService db = getServiceHub().cordaService(PurchaseOrderDatabaseService.class);

            return db.getPurchaseOrderList(sellerVatId, buyerVatId, page, pageSize);

        } catch (Exception ex) {
            throw new FlowException(ex.getMessage());
        }
    }

}