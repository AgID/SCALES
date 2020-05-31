package net.scales.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.scales.states.InvoiceState;

import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.ImmutableList;
import com.r3.corda.lib.tokens.workflows.flows.evolvable.UpdateEvolvableTokenFlow;
import com.r3.corda.lib.tokens.workflows.flows.evolvable.UpdateEvolvableTokenFlowHandler;

import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.FlowSession;
import net.corda.core.flows.InitiatedBy;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.node.ServiceHub;
import net.corda.core.transactions.SignedTransaction;

@StartableByRPC
public class UpdateInvoiceFlow {

    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<String> {

        private final String invoiceNumber;
        private final String competentAuthorityId;
        private final String currentState;
        private final String invoiceOwner;
        private final String approvedSubject;

        public Initiator(String invoiceNumber, String competentAuthorityId, String currentState, String invoiceOwner, String approvedSubject) {
            this.invoiceNumber = invoiceNumber;
            this.competentAuthorityId = competentAuthorityId;
            this.currentState = currentState;
            this.invoiceOwner = invoiceOwner;
            this.approvedSubject = approvedSubject;
        }

        @Override
        @Suspendable
        public String call() throws FlowException {
            try {
                // Get node's service hub
                ServiceHub serviceHub = getServiceHub();

                StateAndRef<InvoiceState> state = null;

                try {
                    // Get the unconsumed InvoiceState from the vault
                    List<StateAndRef<InvoiceState>> states = serviceHub.getVaultService().queryBy(InvoiceState.class).getStates();

                    // Filter state by invoice number
                    state = states.stream().filter(sf->sf.getState().getData().getInvoiceNumber().equals(invoiceNumber)).findAny().get();

                } catch(Exception ex) {}

                if (state == null) {
                    return "";
                }
    
                // Get current state
                InvoiceState oldInvoiceState = state.getState().getData();
    
                // Create new state
                InvoiceState newInvoiceState = new InvoiceState(
                    oldInvoiceState.getLinearId(),
                    oldInvoiceState.getMaintainers(),
                    oldInvoiceState.getIssuer(),
                    competentAuthorityId,
                    currentState,
                    oldInvoiceState.getFileName(),
                    oldInvoiceState.getMessageId(),
                    oldInvoiceState.getDateTimeReceipt(),
                    oldInvoiceState.getDateTimeDelivery(),
                    oldInvoiceState.getNotificationNotes(),
                    oldInvoiceState.getNotificationSignature(),
                    oldInvoiceState.getInvoiceHash(),
                    invoiceOwner,
                    oldInvoiceState.getInvoiceSignature(),
                    approvedSubject,
                    oldInvoiceState.getInvoiceFormat(),
                    oldInvoiceState.getInvoiceNumber(),
                    oldInvoiceState.getInvoiceIssueDate(),
                    oldInvoiceState.getInvoiceTypeCode(),
                    oldInvoiceState.getInvoiceCurrencyCode(),
                    oldInvoiceState.getProjectReference(),
                    oldInvoiceState.getPurchaseOrderReference(),
                    oldInvoiceState.getTenderOrLotReference(),
                    oldInvoiceState.getSellerVatId(),
                    oldInvoiceState.getSellerTaxRegistrationId(),
                    oldInvoiceState.getBuyerVatId(),
                    oldInvoiceState.getBuyerTaxRegistrationId(),
                    oldInvoiceState.getBuyerTaxRegistrationIdSchemeId(),
                    oldInvoiceState.getBuyerElectronicAddress(),
                    oldInvoiceState.getBuyerElectronicAddressSchemeId(),
                    oldInvoiceState.getPaymentDueDate(),
                    oldInvoiceState.getInvoiceTotalAmountWithVat(),
                    oldInvoiceState.getAmountDueForPayment(),
                    oldInvoiceState.getVasId(),
                    oldInvoiceState.getPaidAmountToDate(),
                    oldInvoiceState.getLastPaymentDate(),
                    oldInvoiceState.getType(),
                    oldInvoiceState.getHubId(),
                    oldInvoiceState.getEndEntityId(),
                    System.currentTimeMillis() / 1000
                );

                // Update invoice state by calling UpdateEvolvableToken flow
                SignedTransaction stx = subFlow(new UpdateEvolvableTokenFlow(state, newInvoiceState, ImmutableList.of(), new ArrayList<>()));

                return stx.getId().toString();
    
            } catch (Exception ex) {
                throw new FlowException(ex.getMessage());
            }
        }
    }

    @InitiatedBy(UpdateInvoiceFlow.Initiator.class)
    public static class Responder extends FlowLogic<SignedTransaction> {

        private FlowSession counterSession;

        public Responder(FlowSession counterSession) {
            this.counterSession = counterSession;
        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {
            // To implement the responder flow, simply call the subflow of UpdateEvolvableTokenFlowHandler
            return subFlow(new UpdateEvolvableTokenFlowHandler(counterSession));
        }

    }

}