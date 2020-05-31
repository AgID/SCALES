package net.scales.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.scales.flows.services.InvoiceDatabaseService;
import net.scales.flows.services.MetadataExtractingService;
import net.scales.flows.utils.FileUtils;
import net.scales.states.FileState;
import net.scales.states.InvoiceState;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.r3.corda.lib.tokens.contracts.states.NonFungibleToken;
import com.r3.corda.lib.tokens.contracts.types.IssuedTokenType;
import com.r3.corda.lib.tokens.contracts.utilities.TransactionUtilitiesKt;
import com.r3.corda.lib.tokens.workflows.flows.rpc.CreateEvolvableTokens;
import com.r3.corda.lib.tokens.workflows.flows.rpc.IssueTokens;

import net.corda.core.contracts.TransactionState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.Party;
import net.corda.core.node.NodeInfo;
import net.corda.core.node.ServiceHub;
import net.corda.core.node.services.NetworkMapCache;
import net.corda.core.transactions.SignedTransaction;

@StartableByRPC
public class IssueInvoiceFlow extends FlowLogic<String> {

    private final String hubId;
    private final String entityId;
    private final byte[] invoiceFile;
    private final byte[] sdiFile;

    public IssueInvoiceFlow(String hubId, String entityId, byte[] invoiceFile, byte[] sdiFile) {
        this.hubId = hubId;
        this.entityId = entityId;
        this.invoiceFile = invoiceFile;
        this.sdiFile = sdiFile;
    }

    @Override
    @Suspendable
    public String call() throws FlowException {
        try {
            // Get node's service hub
            ServiceHub serviceHub = getServiceHub();

            // Get network map
            NetworkMapCache network = serviceHub.getNetworkMapCache();

            // Choose the notary for the transaction
            Party notary = network.getNotaryIdentities().get(0);

            // Get a reference of own identity
            Party issuer = getOurIdentity();

            // Declare services
            InvoiceDatabaseService db = serviceHub.cordaService(InvoiceDatabaseService.class);
            MetadataExtractingService extractor = serviceHub.cordaService(MetadataExtractingService.class);

            // Get hash of the invoice file
            String hash = FileUtils.sha256(new ByteArrayInputStream(invoiceFile));

            // Check invoice hash if it has existed
            if (db.checkInvoiceExistence("", hash)) {
                return "0";
            }

            // Extract invoice metadata from file
            Map<String, String> invoiceMetadata = extractor.getInvoiceMetadata(new ByteArrayInputStream(invoiceFile));

            // Chech invoice metadata after extracted
            if (invoiceMetadata == null) {
                return "1";
            }

            // Extract SDI notification metadata from file
            Map<String, String> sdiMetadata = extractor.getSdiNotificationMetadata(new ByteArrayInputStream(sdiFile));

            // Chech SDI notification metadata after extracted
            if (sdiMetadata == null) {
                return "2";
            }

            String type = "";

            // Check invoice type
            if (entityId.equals(invoiceMetadata.get("SellerVATIdentifier"))) {
                type = "Active";

            } else if (entityId.equals(invoiceMetadata.get("BuyerVATIdentifier"))) {
                type = "Passive";

            } else {
                return "3";
            }

            // Create InvoiceState
            InvoiceState invoiceState = new InvoiceState(
                new UniqueIdentifier(),
                ImmutableList.of(issuer),
                issuer,
                sdiMetadata.get("CompetentAuthorityUniqueIdentifier"),
                sdiMetadata.get("CurrentState"),
                sdiMetadata.get("FileName"),
                sdiMetadata.get("MessageId"),
                sdiMetadata.get("DateTimeReceipt"),
                sdiMetadata.get("DateTimeDelivery"),
                sdiMetadata.get("NotificationNotes"),
                sdiMetadata.get("NotificationSignature"),
                hash,
                sdiMetadata.get("InvoiceOwner"),
                sdiMetadata.get("InvoiceSignature"),
                sdiMetadata.get("ApprovedSubject"),
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
                type,
                hubId,
                entityId,
                System.currentTimeMillis() / 1000
            );

            // Create an instance of TransactionState using the invoiceState and the notary
            TransactionState<InvoiceState> transactionInvoiceState = new TransactionState<>(invoiceState, notary);

            // Create the token in the ledger by using the CreateEvolvableTokens flow
            subFlow(new CreateEvolvableTokens(transactionInvoiceState));

            // Create FileState
            FileState fileState = new FileState(
                new UniqueIdentifier(),
                getNodes(network, notary),
                issuer,
                invoiceMetadata.get("InvoiceNumber"),
                hash
            );

            // Create an instance of TransactionState using the fileState and the notary
            TransactionState<FileState> transactionFileState = new TransactionState<>(fileState, notary);

            // Create the token in the ledger by using the CreateEvolvableTokens flow
            subFlow(new CreateEvolvableTokens(transactionFileState));

            // Create an instance of IssuedTokenType, it is used by non-fungible invoice token which would be issued to the owner
            IssuedTokenType issuedToken = new IssuedTokenType(issuer, invoiceState.toPointer());

            // Create an instance of the non-fungible invoice token with the owner as the token holder.
            NonFungibleToken invoiceToken = new NonFungibleToken(issuedToken, issuer, new UniqueIdentifier(), TransactionUtilitiesKt.getAttachmentIdForGenericParam(invoiceState.toPointer()));

            // Issue the invoice token by calling the IssueTokens flow
            SignedTransaction stx = subFlow(new IssueTokens(ImmutableList.of(invoiceToken)));

            // Store the files invoice and SDI notification to database
            db.storeInvoiceFile(invoiceMetadata.get("InvoiceNumber"), new ByteArrayInputStream(invoiceFile), new ByteArrayInputStream(sdiFile));

            return stx.getId().toString() + "_" + invoiceMetadata.get("InvoiceNumber");

        } catch (Exception ex) {
            throw new FlowException(ex.getMessage());
        }
    }

    /**
     * Get all nodes that the node currently is aware of (including itself) but not include notary
     */
    private List<Party> getNodes(NetworkMapCache network, Party notary) {
        List<Party> maintainers = new ArrayList<>();

        // Return all nodes that the node currently is aware of (including itself)
        List<NodeInfo> nodes = network.getAllNodes();

        // Get all node that not include notary
        for (NodeInfo node : nodes) {
            Party party = node.getLegalIdentities().get(0);

            if (party.equals(notary)) {
                continue;
            }
            maintainers.add(party);
        }

        return maintainers;
    }

}