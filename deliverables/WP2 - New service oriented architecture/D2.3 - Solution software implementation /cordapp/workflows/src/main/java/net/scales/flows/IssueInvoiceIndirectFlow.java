package net.scales.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.scales.flows.services.InvoiceDatabaseService;
import net.scales.flows.services.MetadataExtractingService;
import net.scales.flows.utils.FileUtils;
import net.scales.flows.utils.CommonUtils;
import net.scales.states.InvoiceFileState;
import net.scales.states.InvoiceState;

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
public class IssueInvoiceIndirectFlow extends FlowLogic<String> {

    private final Party sdi;
    private final String hubId;
    private final String endEntityId;
    private final byte[] invoice;
    private final byte[] notification;

    public IssueInvoiceIndirectFlow(String hubId, String endEntityId, byte[] invoice, byte[] notification, Party sdi) {
        this.hubId = hubId;
        this.endEntityId = endEntityId;
        this.invoice = invoice;
        this.notification = notification;
        this.sdi = sdi;
    }

    @Override
    @Suspendable
    public String call() throws FlowException {
        try {
            MetadataExtractingService extractor = getServiceHub().cordaService(MetadataExtractingService.class);

            // Extracts invoice metadata from file
            Map<String, String> invoiceMetadata = extractor.getInvoiceMetadata(new ByteArrayInputStream(invoice));

            if (invoiceMetadata == null) {
                return "ERR001";
            }

            // Extracts notification metadata from file
            Map<String, String> notificationMetadata = extractor.getInvoiceNotificationMetadata(new ByteArrayInputStream(notification));

            if (notificationMetadata == null) {
                return "ERR002";
            }

            // Checks if user has permission
            if (!endEntityId.equals(invoiceMetadata.get("SellerVATIdentifier")) && !endEntityId.equals(invoiceMetadata.get("BuyerVATIdentifier"))) {
                return "ERR003";
            }

            InvoiceDatabaseService db = getServiceHub().cordaService(InvoiceDatabaseService.class);

            String txId;

            // Gets hash of the invoice file
            String invoiceHash = FileUtils.sha256(new ByteArrayInputStream(invoice));

            InvoiceFileState fileState = getFileStateByHash(invoiceHash);

            // Invoice wasn't minted yet
            if (fileState == null) {
                UniqueIdentifier identifier = new UniqueIdentifier();

                // Saves InvoiceFileState to vault of all nodes
                createFileState(identifier.toString(), invoiceHash);

                // Issues invoice token and save InvoiceState to vault of this node
                txId = issueInvoiceToken(createInvoiceState(identifier, invoiceMetadata, notificationMetadata, invoiceHash)) + "_" + identifier.toString();

                // Saves the files invoice and notification to database of this node
                db.insertInvoiceFile(identifier.toString(), new ByteArrayInputStream(invoice), new ByteArrayInputStream(notification));

            // Invoice was minted
            } else {
                // Checks if invoice data had on this node yet
                if (getInvoiceStateById(fileState.getId()) != null) {
                    return "ERR004";
                }

                // Syncs InvoiceState from other node
                txId = subFlow(new SyncInvoiceIndirectFlow.Initiator(hubId, endEntityId, fileState.getIssuer(), fileState.getId())) + "_" + fileState.getId();

                // Saves the files invoice and notification to database of this node
                db.insertInvoiceFile(fileState.getId(), new ByteArrayInputStream(invoice), new ByteArrayInputStream(notification));
            }

            return txId;

        } catch (Exception ex) {
            throw new FlowException(ex.getMessage());
        }
    }

    @Suspendable
    private String issueInvoiceToken(InvoiceState state) throws FlowException {
        Party issuer = getOurIdentity();

        // Creates an instance of IssuedTokenType, it is used by non-fungible invoice token which would be issued to the owner
        IssuedTokenType issuedToken = new IssuedTokenType(issuer, state.toPointer());

        // Creates an instance of the non-fungible invoice token with the owner as the token holder.
        NonFungibleToken invoiceToken = new NonFungibleToken(issuedToken, issuer, new UniqueIdentifier(), TransactionUtilitiesKt.getAttachmentIdForGenericParam(state.toPointer()));
    
        // Issues the invoice token by calling the IssueTokens flow
        SignedTransaction stx = subFlow(new IssueTokens(ImmutableList.of(invoiceToken)));

        return stx.getId().toString();
    }

    @Suspendable
    private InvoiceState createInvoiceState(UniqueIdentifier identifier, Map<String, String> invoiceMetadata, Map<String, String> notificationMetadata, String invoiceHash) throws FlowException {
        Party issuer = getOurIdentity();

        InvoiceState state = new InvoiceState(
            identifier,
            ImmutableList.of(issuer),
            issuer,
            notificationMetadata.get("CompetentAuthorityUniqueIdentifier"),
            notificationMetadata.get("CurrentState"),
            notificationMetadata.get("FileName"),
            notificationMetadata.get("MessageId"),
            notificationMetadata.get("DateTimeReceipt"),
            notificationMetadata.get("DateTimeDelivery"),
            notificationMetadata.get("NotificationNotes"),
            notificationMetadata.get("NotificationSignature"),
            invoiceHash,
            notificationMetadata.get("InvoiceOwner"),
            notificationMetadata.get("InvoiceSignature"),
            notificationMetadata.get("ApprovedSubject"),
            invoiceMetadata.get("InvoiceFormat"),
            invoiceMetadata.get("InvoiceNumber"),
            invoiceMetadata.get("InvoiceIssueDate"),
            invoiceMetadata.get("InvoiceTypeCode"),
            invoiceMetadata.get("InvoiceCurrencyCode"),
            invoiceMetadata.get("ProjectReference"),
            invoiceMetadata.get("PurchaseOrderReference"),
            invoiceMetadata.get("TenderOrLotReference"),
            invoiceMetadata.get("SellerVATIdentifier"),
            invoiceMetadata.get("SellerTaxRegistrationIdentifier"),
            invoiceMetadata.get("BuyerVATIdentifier"),
            invoiceMetadata.get("BuyerTaxRegistrationIdentifier"),
            invoiceMetadata.get("BuyerTaxRegistrationIdentifierSchemeIdentifier"),
            invoiceMetadata.get("BuyerElectronicAddress"),
            invoiceMetadata.get("BuyerElectronicAddressSchemeIdentifier"),
            invoiceMetadata.get("PaymentDueDate"),
            invoiceMetadata.get("InvoiceTotalAmountWithVAT"),
            invoiceMetadata.get("AmountDueForPayment"),
            "",
            "",
            "",
            "",
            "",
            hubId,
            endEntityId,
            System.currentTimeMillis() / 1000
        );

        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        // Creates an instance of TransactionState using the state and the notary
        TransactionState<InvoiceState> transactionState = new TransactionState<>(state, notary);

        // Creates the token in the ledger by using the CreateEvolvableTokens flow
        subFlow(new CreateEvolvableTokens(transactionState));

        return state;
    }

    @Suspendable
    private InvoiceFileState createFileState(String id, String hash) throws FlowException {
        NetworkMapCache network = getServiceHub().getNetworkMapCache();

        Party notary = network.getNotaryIdentities().get(0);

        InvoiceFileState state = new InvoiceFileState(
            new UniqueIdentifier(),
            CommonUtils.getAllNodes(network, notary, sdi),
            getOurIdentity(),
            id,
            hash
        );

        // Creates an instance of TransactionState using the state and the notary
        TransactionState<InvoiceFileState> transactionState = new TransactionState<>(state, notary);

        // Creates the token in the ledger by using the CreateEvolvableTokens flow
        subFlow(new CreateEvolvableTokens(transactionState));

        return state;
    }

    private InvoiceFileState getFileStateByHash(String hash) {
        try {
            // Gets the unconsumed InvoiceFileState from the vault
            List<StateAndRef<InvoiceFileState>> states = getServiceHub().getVaultService().queryBy(InvoiceFileState.class).getStates();

            // Filters state by file hash
            StateAndRef<InvoiceFileState> state = states.stream().filter(sf->sf.getState().getData().getHash().equals(hash)).findAny().get();

            return state.getState().getData();

        } catch(Exception ex) {
            return null;
        }
    }

    private InvoiceState getInvoiceStateById(String id) {
        try {
            // Gets the unconsumed InvoiceState from the vault
            List<StateAndRef<InvoiceState>> states = getServiceHub().getVaultService().queryBy(InvoiceState.class).getStates();

            // Filters state by id
            StateAndRef<InvoiceState> state = states.stream().filter(sf->sf.getState().getData().getLinearId().toString().equals(id)).findAny().get();

            return state.getState().getData();

        } catch(Exception ex) {
            return null;
        }
    }

}