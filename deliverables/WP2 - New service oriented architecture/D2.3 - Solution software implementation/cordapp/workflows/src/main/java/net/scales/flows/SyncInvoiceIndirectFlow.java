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
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;

public class SyncInvoiceIndirectFlow {

    @InitiatingFlow
    public static class Initiator extends FlowLogic<String> {

        private final Party otherParty;
        private final String invoiceId;
        private final String hubId;
        private final String endEntityId;

        public Initiator(String hubId, String endEntityId, Party otherParty, String invoiceId) {
            this.hubId = hubId;
            this.endEntityId = endEntityId;
            this.otherParty = otherParty;
            this.invoiceId = invoiceId;
        }

        @Override
        @Suspendable
        public String call() throws FlowException {
            return initiateFlow(otherParty).sendAndReceive(String.class, hubId + " ; " + endEntityId + " ; " + invoiceId).unwrap(it -> it);
        }
    }

    @InitiatedBy(SyncInvoiceIndirectFlow.Initiator.class)
    public static class Responder extends FlowLogic<Void> {

        private FlowSession otherPartySession;

        public Responder(FlowSession otherPartySession) {
            this.otherPartySession = otherPartySession;
        }

        @Suspendable
        @Override
        public Void call() throws FlowException {
            try {
                // Receives data from other node
                String data = otherPartySession.receive(String.class).unwrap(it -> it);

                String[] elements = data.split(" ; ", 3);

                // Gets the unconsumed InvoiceState from the vault
                List<StateAndRef<InvoiceState>> states = getServiceHub().getVaultService().queryBy(InvoiceState.class).getStates();
    
                // Filters state by id
                StateAndRef<InvoiceState> state = states.stream().filter(sf->sf.getState().getData().getLinearId().toString().equals(elements[2])).findAny().get();

                // Updates invoice state for nodes that involves to invoice
                String txId = subFlow(new SyncInvoiceIndirectFlow.UpdateStateInitiator(elements[0], elements[1], state, otherPartySession.getCounterparty()));

                // Responses data to node that requested
                otherPartySession.send(txId);

            } catch(Exception ex) {
                otherPartySession.send(null);
            }

            return null;
        }

    }

    @InitiatingFlow
    public static class UpdateStateInitiator extends FlowLogic<String> {

        private final StateAndRef<InvoiceState> state;
        private final Party otherParty;
        private final String hubId;
        private final String endEntityId;

        public UpdateStateInitiator(String hubId, String endEntityId, StateAndRef<InvoiceState> state, Party otherParty) {
            this.hubId = hubId;
            this.endEntityId = endEntityId;
            this.state = state;
            this.otherParty = otherParty;
        }

        @Override
        @Suspendable
        public String call() throws FlowException {
            InvoiceState currentState = state.getState().getData();

            // Adds new maintainer
            List<Party> maintainers = new ArrayList<>(currentState.getMaintainers());
            maintainers.add(otherParty);

            InvoiceState newState = new InvoiceState(
                currentState.getLinearId(),
                maintainers,
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
                currentState.getAmountDueForPayment(),
                currentState.getVasId(),
                currentState.getPaidAmountToDate(),
                currentState.getLastPaymentDate(),
                currentState.getLastUpdate(),
                currentState.getPaid(),
                hubId,
                endEntityId,
                System.currentTimeMillis() / 1000
            );

            List<FlowSession> sessions = new ArrayList<>();

            for (Party party : maintainers) {
                sessions.add(initiateFlow(party));
            }

            // Updates invoice state by calling UpdateEvolvableToken flow
            SignedTransaction stx = subFlow(new UpdateEvolvableTokenFlow(state, newState, sessions, new ArrayList<>()));

            return stx.getId().toString();
        }
    }

    @InitiatedBy(SyncInvoiceIndirectFlow.UpdateStateInitiator.class)
    public static class UpdateStateResponder extends FlowLogic<SignedTransaction> {

        private FlowSession counterSession;

        public UpdateStateResponder(FlowSession counterSession) {
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