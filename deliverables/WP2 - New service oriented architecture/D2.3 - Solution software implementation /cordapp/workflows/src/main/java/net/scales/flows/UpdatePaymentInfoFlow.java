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

public class UpdatePaymentInfoFlow {

    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<String> {

        private final String invoiceId;
        private final String vasId;
        private final String paidAmountToDate;
        private final String lastPaymentDate;
        private final String amountDueForPayment;
        private final String lastUpdate;
        private final String paid;

        public Initiator(String invoiceId, String vasId, String paidAmountToDate, String lastPaymentDate, String amountDueForPayment, String lastUpdate, String paid) {
            this.invoiceId = invoiceId;
            this.vasId = vasId;
            this.paidAmountToDate = paidAmountToDate;
            this.lastPaymentDate = lastPaymentDate;
            this.amountDueForPayment = amountDueForPayment;
            this.lastUpdate = lastUpdate;
            this.paid = paid;
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
                InvoiceState currentState = state.getState().getData();
    
                // Creates new state
                InvoiceState newState = new InvoiceState(
                    currentState.getLinearId(),
                    currentState.getMaintainers(),
                    currentState.getIssuer(),
                    currentState.getCompetentAuthorityId(),
                    currentState.getCurrentState(),
                    currentState.getFileName(),
                    currentState.getMessageId(),
                    currentState.getDateTimeReceipt(),
                    currentState.getDateTimeDelivery(),
                    currentState.getNotificationNotes(),
                    currentState.getNotificationSignature(),
                    currentState.getInvoiceHash(),
                    currentState.getInvoiceOwner(),
                    currentState.getInvoiceSignature(),
                    currentState.getApprovedSubject(),
                    currentState.getInvoiceFormat(),
                    currentState.getInvoiceNumber(),
                    currentState.getInvoiceIssueDate(),
                    currentState.getInvoiceTypeCode(),
                    currentState.getInvoiceCurrencyCode(),
                    currentState.getProjectReference(),
                    currentState.getPurchaseOrderReference(),
                    currentState.getTenderOrLotReference(),
                    currentState.getSellerVatId(),
                    currentState.getSellerTaxRegistrationId(),
                    currentState.getBuyerVatId(),
                    currentState.getBuyerTaxRegistrationId(),
                    currentState.getBuyerTaxRegistrationIdSchemeId(),
                    currentState.getBuyerElectronicAddress(),
                    currentState.getBuyerElectronicAddressSchemeId(),
                    currentState.getPaymentDueDate(),
                    currentState.getInvoiceTotalAmountWithVat(),
                    amountDueForPayment,
                    vasId,
                    paidAmountToDate,
                    lastPaymentDate,
                    lastUpdate,
                    paid,
                    currentState.getHubId(),
                    currentState.getEndEntityId(),
                    System.currentTimeMillis() / 1000
                );

                List<FlowSession> sessions = new ArrayList<>();

                for (Party party : currentState.getMaintainers()) {
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

    @InitiatedBy(UpdatePaymentInfoFlow.Initiator.class)
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