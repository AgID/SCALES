package net.scales.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.scales.flows.services.PurchaseOrderDatabaseService;
import net.scales.flows.services.MetadataExtractingService;
import net.scales.flows.utils.FileUtils;
import net.scales.flows.utils.CommonUtils;
import net.scales.states.PurchaseOrderFileState;
import net.scales.states.PurchaseOrderState;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.r3.corda.lib.tokens.contracts.states.NonFungibleToken;
import com.r3.corda.lib.tokens.contracts.types.IssuedTokenType;
import com.r3.corda.lib.tokens.contracts.utilities.TransactionUtilitiesKt;
import com.r3.corda.lib.tokens.workflows.flows.rpc.CreateEvolvableTokens;
import com.r3.corda.lib.tokens.workflows.flows.rpc.IssueTokens;

import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.TransactionState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.Party;
import net.corda.core.node.services.NetworkMapCache;
import net.corda.core.transactions.SignedTransaction;

@StartableByRPC
public class IssuePurchaseOrderIndirectFlow extends FlowLogic<String> {

    private final Party sdi;
    private final String hubId;
    private final String endEntityId;
    private final byte[] order;
    private final byte[] notification;

    public IssuePurchaseOrderIndirectFlow(String hubId, String endEntityId, byte[] order, byte[] notification, Party sdi) {
        this.hubId = hubId;
        this.endEntityId = endEntityId;
        this.order = order;
        this.notification = notification;
        this.sdi = sdi;
    }

    @Override
    @Suspendable
    public String call() throws FlowException {
        try {
            MetadataExtractingService extractor = getServiceHub().cordaService(MetadataExtractingService.class);

            // Extracts order metadata from file
            Map<String, String> orderMetadata = extractor.getOrderMetadata(new ByteArrayInputStream(order));

            if (orderMetadata == null) {
                return "ERR001";
            }

            // Extracts notification metadata from file
            Map<String, String> notificationMetadata = extractor.getOrderNotificationMetadata(new ByteArrayInputStream(notification));

            if (notificationMetadata == null) {
                return "ERR002";
            }

            // // Checks if user has permission
            // if (!endEntityId.equals(orderMetadata.get("SellerVATIdentifier")) && !endEntityId.equals(orderMetadata.get("BuyerVATIdentifier"))) {
            //     return "ERR003";
            // }

            PurchaseOrderDatabaseService db = getServiceHub().cordaService(PurchaseOrderDatabaseService.class);

            String txId;

            // Gets hash of the order file
            String orderHash = FileUtils.sha256(new ByteArrayInputStream(order));

            PurchaseOrderFileState fileState = getFileStateByHash(orderHash);

            // Order doesn't exist
            if (fileState == null) {
                UniqueIdentifier identifier = new UniqueIdentifier();

                // Saves PurchaseOrderFileState to vault of all nodes
                createFileState(identifier.toString(), orderHash);

                // Issues order token and save PurchaseOrderState to vault of this node
                txId = issueOrderToken(createOrderState(identifier, orderMetadata, notificationMetadata, orderHash)) + "_" + identifier.toString();

                // Saves the files order and notification to database of this node
                db.insertPurchaseOrderFile(identifier.toString(), new ByteArrayInputStream(order), new ByteArrayInputStream(notification));

            // Order exists
            } else {
                // Checks if order data had on this node yet
                if (getOrderStateById(fileState.getId()) != null) {
                    return "ERR004";
                }

                // Syncs PurchaseOrderState from other node
                txId = subFlow(new SyncPurchaseOrderFlow.Initiator(hubId, endEntityId, fileState.getIssuer(), fileState.getId())) + "_" + fileState.getId();

                // Saves the files order and notification to database of this node
                db.insertPurchaseOrderFile(fileState.getId(), new ByteArrayInputStream(order), new ByteArrayInputStream(notification));
            }

            return txId;

        } catch (Exception ex) {
            throw new FlowException(ex.getMessage());
        }
    }

    @Suspendable
    private String issueOrderToken(PurchaseOrderState state) throws FlowException {
        Party issuer = getOurIdentity();

        // Creates an instance of IssuedTokenType, it is used by non-fungible order token which would be issued to the owner
        IssuedTokenType issuedToken = new IssuedTokenType(issuer, state.toPointer());

        // Creates an instance of the non-fungible order token with the owner as the token holder.
        NonFungibleToken orderToken = new NonFungibleToken(issuedToken, issuer, new UniqueIdentifier(), TransactionUtilitiesKt.getAttachmentIdForGenericParam(state.toPointer()));
    
        // Issues the order token by calling the IssueTokens flow
        SignedTransaction stx = subFlow(new IssueTokens(ImmutableList.of(orderToken)));

        return stx.getId().toString();
    }

    @Suspendable
    private PurchaseOrderState createOrderState(UniqueIdentifier identifier, Map<String, String> orderMetadata, Map<String, String> notificationMetadata, String orderHash) throws FlowException {
        Party issuer = getOurIdentity();

        PurchaseOrderState state = new PurchaseOrderState(
            identifier,
            ImmutableList.of(issuer),
            issuer,
            notificationMetadata.get("IdT"),
            notificationMetadata.get("ReceiverID"),
            "",
            notificationMetadata.get("CurrentState"),
            notificationMetadata.get("FileName"),
            notificationMetadata.get("MessageId"),
            notificationMetadata.get("DateTimeReceipt"),
            notificationMetadata.get("DateTimeDelivery"),
            notificationMetadata.get("NotificationNotes"),
            notificationMetadata.get("NotificationSignature"),
            orderHash,
            "",
            orderMetadata.get("OrderSender"),
            orderMetadata.get("OrderReceiver"),
            orderMetadata.get("OrderCreationDateAndTime"),
            orderMetadata.get("OrderProfile"),
            orderMetadata.get("OrderID"),
            orderMetadata.get("OrderIssueDate"),
            orderMetadata.get("OrderTypeCode"),
            orderMetadata.get("OrderCurrencyCode"),
            orderMetadata.get("OrderDocumentReference"),
            orderMetadata.get("OriginatorDocumentReference"),
            orderMetadata.get("Contract"),
            orderMetadata.get("AccountingCustomerVATIdentifier"),
            orderMetadata.get("AccountingCustomerElectronicAddress"),
            orderMetadata.get("AccountingCustomerAddressSchemeIdentifier"),
            orderMetadata.get("PayableAmount"),
            notificationMetadata.get("ApprovedSubject"),
            orderMetadata.get("BuyerVATIdentifier"),
            orderMetadata.get("BuyerElectronicAddress"),
            orderMetadata.get("BuyerElectronicAddressSchemeIdentifier"),
            orderMetadata.get("SellerVATIdentifier"),
            orderMetadata.get("SellerElectronicAddress"),
            orderMetadata.get("SellerElectronicAddress"),
            orderMetadata.get("SellerPartyIdentifier"),
            hubId,
            endEntityId,
            System.currentTimeMillis() / 1000
        );

        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        // Creates an instance of TransactionState using the state and the notary
        TransactionState<PurchaseOrderState> transactionState = new TransactionState<>(state, notary);

        // Creates the token in the ledger by using the CreateEvolvableTokens flow
        subFlow(new CreateEvolvableTokens(transactionState));

        return state;
    }

    @Suspendable
    private PurchaseOrderFileState createFileState(String id, String hash) throws FlowException {
        NetworkMapCache network = getServiceHub().getNetworkMapCache();

        Party notary = network.getNotaryIdentities().get(0);

        PurchaseOrderFileState state = new PurchaseOrderFileState(
            new UniqueIdentifier(),
            CommonUtils.getAllNodes(network, notary, sdi),
            getOurIdentity(),
            id,
            hash
        );

        // Creates an instance of TransactionState using the state and the notary
        TransactionState<PurchaseOrderFileState> transactionState = new TransactionState<>(state, notary);

        // Creates the token in the ledger by using the CreateEvolvableTokens flow
        subFlow(new CreateEvolvableTokens(transactionState));

        return state;
    }

    private PurchaseOrderFileState getFileStateByHash(String hash) {
        try {
            // Gets the unconsumed PurchaseOrderFileState from the vault
            List<StateAndRef<PurchaseOrderFileState>> states = getServiceHub().getVaultService().queryBy(PurchaseOrderFileState.class).getStates();

            // Filters state by order hash
            StateAndRef<PurchaseOrderFileState> state = states.stream().filter(sf->sf.getState().getData().getHash().equals(hash)).findAny().get();

            return state.getState().getData();

        } catch(Exception ex) {
            return null;
        }
    }

    private PurchaseOrderState getOrderStateById(String id) {
        try {
            // Gets the unconsumed PurchaseOrderState from the vault
            List<StateAndRef<PurchaseOrderState>> states = getServiceHub().getVaultService().queryBy(PurchaseOrderState.class).getStates();

            // Filters state by id
            StateAndRef<PurchaseOrderState> state = states.stream().filter(sf->sf.getState().getData().getLinearId().toString().equals(id)).findAny().get();

            return state.getState().getData();

        } catch(Exception ex) {
            return null;
        }
    }

}