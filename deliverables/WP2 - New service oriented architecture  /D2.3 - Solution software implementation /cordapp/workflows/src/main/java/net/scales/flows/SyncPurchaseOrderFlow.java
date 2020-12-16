package net.scales.flows;

import co.paralleluniverse.fibers.Suspendable;

import net.scales.states.PurchaseOrderState;

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

public class SyncPurchaseOrderFlow {

    @InitiatingFlow
    public static class Initiator extends FlowLogic<String> {

        private final Party otherParty;
        private final String orderId;
        private final String hubId;
        private final String endEntityId;

        public Initiator(String hubId, String endEntityId, Party otherParty, String orderId) {
            this.hubId = hubId;
            this.endEntityId = endEntityId;
            this.otherParty = otherParty;
            this.orderId = orderId;
        }

        @Override
        @Suspendable
        public String call() throws FlowException {
            return initiateFlow(otherParty).sendAndReceive(String.class, hubId + " ; " + endEntityId + " ; " + orderId).unwrap(it -> it);
        }
    }

    @InitiatedBy(SyncPurchaseOrderFlow.Initiator.class)
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

                // Gets the unconsumed PurchaseOrderState from the vault
                List<StateAndRef<PurchaseOrderState>> states = getServiceHub().getVaultService().queryBy(PurchaseOrderState.class).getStates();
    
                // Filters state by id
                StateAndRef<PurchaseOrderState> state = states.stream().filter(sf->sf.getState().getData().getLinearId().toString().equals(elements[2])).findAny().get();

                // Updates order state for nodes that involves to order
                String txId = subFlow(new SyncPurchaseOrderFlow.UpdateStateInitiator(elements[0], elements[1], state, otherPartySession.getCounterparty()));

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

        private final StateAndRef<PurchaseOrderState> state;
        private final Party otherParty;
        private final String hubId;
        private final String endEntityId;

        public UpdateStateInitiator(String hubId, String endEntityId, StateAndRef<PurchaseOrderState> state, Party otherParty) {
            this.hubId = hubId;
            this.endEntityId = endEntityId;
            this.state = state;
            this.otherParty = otherParty;
        }

        @Override
        @Suspendable
        public String call() throws FlowException {
            PurchaseOrderState currentState = state.getState().getData();

            // Adds new maintainer
            List<Party> maintainers = new ArrayList<>(currentState.getMaintainers());
            maintainers.add(otherParty);

            PurchaseOrderState newState = new PurchaseOrderState(
                currentState.getLinearId(),
                maintainers,
                currentState.getIssuer(),
                currentState.getIdt(),
                currentState.getReceiverId(),
                currentState.getPecMessageId(),
                currentState.getCurrentState(),
                currentState.getFileName(),
                currentState.getMessageId(),
                currentState.getDateTimeReceipt(),
                currentState.getDateTimeDelivery(),
                currentState.getNotificationNotes(),
                currentState.getNotificationSignature(),
                currentState.getOrderHash(),
                currentState.getOrderOriginalHash(),
                currentState.getOrderSender(),
                currentState.getOrderReceiver(),
                currentState.getOrderCreationDateAndTime(),
                currentState.getOrderProfile(),
                currentState.getOrderId(),
                currentState.getOrderIssueDate(),
                currentState.getOrderTypeCode(),
                currentState.getOrderCurrencyCode(),
                currentState.getOrderDocumentReference(),
                currentState.getOriginatorDocumentReference(),
                currentState.getContract(),
                currentState.getAccountingCustomerPartyVatId(),
                currentState.getAccountingCustomerPartyElectronicAddress(),
                currentState.getAccountingCustomerPartyElectronicAddressSchemeId(),
                currentState.getPayableAmount(),
                currentState.getApprovedSubject(),
                currentState.getBuyerVatId(),
                currentState.getBuyerElectronicAddress(),
                currentState.getBuyerElectronicAddressSchemeId(),
                currentState.getSellerVatId(),
                currentState.getSellerElectronicAddress(),
                currentState.getSellerElectronicAddressSchemeId(),
                currentState.getSellerPartyId(),
                hubId,
                endEntityId,
                System.currentTimeMillis() / 1000
            );

            List<FlowSession> sessions = new ArrayList<>();

            for (Party party : maintainers) {
                sessions.add(initiateFlow(party));
            }

            // Updates order state by calling UpdateEvolvableToken flow
            SignedTransaction stx = subFlow(new UpdateEvolvableTokenFlow(state, newState, sessions, new ArrayList<>()));

            return stx.getId().toString();
        }
    }

    @InitiatedBy(SyncPurchaseOrderFlow.UpdateStateInitiator.class)
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