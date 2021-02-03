package net.scales.flows;

import co.paralleluniverse.fibers.Suspendable;

import net.scales.states.InvoiceState;

import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.FlowSession;
import net.corda.core.flows.InitiatedBy;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.identity.Party;

public class SyncInvoiceNotificationDirectFlow {

    @InitiatingFlow
    public static class Initiator extends FlowLogic<InvoiceState> {

        private final Party otherParty;
        private final InvoiceState state;

        public Initiator(Party otherParty, InvoiceState state) {
            this.otherParty = otherParty;
            this.state = state;
        }

        @Override
        @Suspendable
        public InvoiceState call() throws FlowException {
            return initiateFlow(otherParty).sendAndReceive(InvoiceState.class, state).unwrap(it -> it);
        }
    }

    @InitiatedBy(SyncInvoiceNotificationDirectFlow.Initiator.class)
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
                InvoiceState currentState = otherPartySession.receive(InvoiceState.class).unwrap(it -> it);

                InvoiceState newState = new InvoiceState(
                    currentState.getLinearId(),
                    currentState.getMaintainers(),
                    currentState.getIssuer(),
                    "CompetentAuthorityUniqueIdentifier",
                    "CurrentState",
                    "FileName",
                    "MessageId",
                    "DateTimeReceipt",
                    "DateTimeDelivery",
                    "NotificationNotes",
                    "NotificationSignature",
                    currentState.getInvoiceHash(),
                    "InvoiceOwner",
                    "InvoiceSignature",
                    "ApprovedSubject",
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
                    currentState.getHubId(),
                    currentState.getEndEntityId(),
                    currentState.getTime()
                );

                // Responses data to node that requested
                otherPartySession.send(newState);

            } catch(Exception ex) {
                otherPartySession.send(null);
            }

            return null;
        }

    }

}