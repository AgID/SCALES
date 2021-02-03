package net.scales.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.scales.states.InvoiceState;

import java.util.ArrayList;
import java.util.List;
import com.r3.corda.lib.tokens.workflows.flows.evolvable.UpdateEvolvableTokenFlow;
import com.r3.corda.lib.tokens.workflows.flows.evolvable.UpdateEvolvableTokenFlowHandler;

import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.FlowSession;
import net.corda.core.flows.InitiatedBy;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;

public class UpdateInvoiceFlow {

    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<String> {

        private final String invoiceId;
        private final String competentAuthorityId;
        private final String currentState;
        private final String invoiceOwner;
        private final String approvedSubject;

        public Initiator(String invoiceId, String competentAuthorityId, String currentState, String invoiceOwner, String approvedSubject) {
            this.invoiceId = invoiceId;
            this.competentAuthorityId = competentAuthorityId;
            this.currentState = currentState;
            this.invoiceOwner = invoiceOwner;
            this.approvedSubject = approvedSubject;
        }

        @Override
        @Suspendable
        public String call() throws FlowException {
            try {
                StateAndRef<InvoiceState> state = null;

                try {
                    // Gets the unconsumed InvoiceState from the vault
                    List<StateAndRef<InvoiceState>> states = getServiceHub().getVaultService().queryBy(InvoiceState.class).getStates();

                    // Filters state by id
                    state = states.stream().filter(sf->sf.getState().getData().getLinearId().toString().equals(invoiceId)).findAny().get();

                } catch(Exception ex) {
                    return null;
                }
    
                // Gets current state
                InvoiceState oldState = state.getState().getData();
    
                // Creates new state
                InvoiceState newState = new InvoiceState(
                    oldState.getLinearId(),
                    oldState.getMaintainers(),
                    oldState.getIssuer(),
                    competentAuthorityId,
                    currentState,
                    oldState.getFileName(),
                    oldState.getMessageId(),
                    oldState.getDateTimeReceipt(),
                    oldState.getDateTimeDelivery(),
                    oldState.getNotificationNotes(),
                    oldState.getNotificationSignature(),
                    oldState.getInvoiceHash(),
                    invoiceOwner,
                    oldState.getInvoiceSignature(),
                    approvedSubject,
                    oldState.getInvoiceFormat(),
                    oldState.getInvoiceNumber(),
                    oldState.getInvoiceIssueDate(),
                    oldState.getInvoiceTypeCode(),
                    oldState.getInvoiceCurrencyCode(),
                    oldState.getProjectReference(),
                    oldState.getPurchaseOrderReference(),
                    oldState.getTenderOrLotReference(),
                    oldState.getSellerVatId(),
                    oldState.getSellerTaxRegistrationId(),
                    oldState.getBuyerVatId(),
                    oldState.getBuyerTaxRegistrationId(),
                    oldState.getBuyerTaxRegistrationIdSchemeId(),
                    oldState.getBuyerElectronicAddress(),
                    oldState.getBuyerElectronicAddressSchemeId(),
                    oldState.getPaymentDueDate(),
                    oldState.getInvoiceTotalAmountWithVat(),
                    oldState.getAmountDueForPayment(),
                    oldState.getVasId(),
                    oldState.getPaidAmountToDate(),
                    oldState.getLastPaymentDate(),
                    oldState.getLastUpdate(),
                    oldState.getPaid(),
                    oldState.getHubId(),
                    oldState.getEndEntityId(),
                    System.currentTimeMillis() / 1000
                );

                List<FlowSession> sessions = new ArrayList<>();

                for (Party party : oldState.getMaintainers()) {
                    sessions.add(initiateFlow(party));
                }

                // Updates invoice state by calling UpdateEvolvableToken flow
                SignedTransaction stx = subFlow(new UpdateEvolvableTokenFlow(state, newState, sessions, new ArrayList<>()));

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