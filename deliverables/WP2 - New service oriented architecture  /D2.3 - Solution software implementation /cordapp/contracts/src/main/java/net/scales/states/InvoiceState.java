package net.scales.states;

import net.scales.contracts.InvoiceContract;
import net.scales.schemas.InvoiceSchemaV1;

import com.google.common.collect.ImmutableList;
import com.r3.corda.lib.tokens.contracts.states.EvolvableTokenType;
import com.r3.corda.lib.tokens.contracts.types.TokenPointer;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.LinearPointer;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.Party;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.schemas.QueryableState;

import org.jetbrains.annotations.NotNull;

import java.util.List;

@BelongsToContract(InvoiceContract.class)
public class InvoiceState extends EvolvableTokenType implements QueryableState {

    private final UniqueIdentifier linearId;
    private final List<Party> maintainers;
    private final Party issuer;
    private final int fractionDigits = 0;

    // Properties of Invoice
    // Some of these values may evolve over time
    private final String competentAuthorityId;
    private final String currentState;
    private final String fileName;
    private final String messageId;
    private final String dateTimeReceipt;
    private final String dateTimeDelivery;
    private final String notificationNotes;
    private final String notificationSignature;
    private final String invoiceHash;
    private final String invoiceOwner;
    private final String invoiceSignature;
    private final String approvedSubject;
    private final String invoiceFormat;
    private final String invoiceNumber;
    private final String invoiceIssueDate;
    private final String invoiceTypeCode;
    private final String invoiceCurrencyCode;
    private final String projectReference;
    private final String purchaseOrderReference;
    private final String tenderOrLotReference;
    private final String sellerVatId;
    private final String sellerTaxRegistrationId;
    private final String buyerVatId;
    private final String buyerTaxRegistrationId;
    private final String buyerTaxRegistrationIdSchemeId;
    private final String buyerElectronicAddress;
    private final String buyerElectronicAddressSchemeId;
    private final String paymentDueDate;
    private final String invoiceTotalAmountWithVat;

    private final String amountDueForPayment;
    private final String vasId;
    private final String paidAmountToDate;
    private final String lastPaymentDate;
    private final String lastUpdate;
    private final String paid;

    private final String hubId;
    private final String endEntityId;
    private final long time;

    public InvoiceState(
        UniqueIdentifier linearId,
        List<Party> maintainers,
        Party issuer,
        String competentAuthorityId,
        String currentState,
        String fileName,
        String messageId,
        String dateTimeReceipt,
        String dateTimeDelivery,
        String notificationNotes,
        String notificationSignature,
        String invoiceHash,
        String invoiceOwner,
        String invoiceSignature,
        String approvedSubject,
        String invoiceFormat,
        String invoiceNumber,
        String invoiceIssueDate,
        String invoiceTypeCode,
        String invoiceCurrencyCode,
        String projectReference,
        String purchaseOrderReference,
        String tenderOrLotReference,
        String sellerVatId,
        String sellerTaxRegistrationId,
        String buyerVatId,
        String buyerTaxRegistrationId,
        String buyerTaxRegistrationIdSchemeId,
        String buyerElectronicAddress,
        String buyerElectronicAddressSchemeId,
        String paymentDueDate,
        String invoiceTotalAmountWithVat,
        String amountDueForPayment,
        String vasId,
        String paidAmountToDate,
        String lastPaymentDate,
        String lastUpdate,
        String paid,
        String hubId,
        String endEntityId,
        long time
    ) {
        this.linearId = linearId;
        this.maintainers = maintainers;
        this.issuer = issuer;

        this.competentAuthorityId = competentAuthorityId;
        this.currentState = currentState;
        this.fileName = fileName;
        this.messageId = messageId;
        this.dateTimeReceipt = dateTimeDelivery;
        this.dateTimeDelivery = dateTimeDelivery;
        this.notificationNotes = notificationNotes;
        this.notificationSignature = notificationSignature;
        this.invoiceHash = invoiceHash;
        this.invoiceOwner = invoiceOwner;
        this.invoiceSignature = invoiceSignature;
        this.approvedSubject = approvedSubject;
        this.invoiceFormat = invoiceFormat;
        this.invoiceNumber = invoiceNumber;
        this.invoiceIssueDate = invoiceIssueDate;
        this.invoiceTypeCode = invoiceTypeCode;
        this.invoiceCurrencyCode = invoiceCurrencyCode;
        this.projectReference = projectReference;
        this.purchaseOrderReference = purchaseOrderReference;
        this.tenderOrLotReference = tenderOrLotReference;
        this.sellerVatId = sellerVatId;
        this.sellerTaxRegistrationId = sellerTaxRegistrationId;
        this.buyerVatId = buyerVatId;
        this.buyerTaxRegistrationId = buyerTaxRegistrationId;
        this.buyerTaxRegistrationIdSchemeId = buyerTaxRegistrationIdSchemeId;
        this.buyerElectronicAddress = buyerElectronicAddress;
        this.buyerElectronicAddressSchemeId = buyerElectronicAddressSchemeId;
        this.paymentDueDate = paymentDueDate;
        this.invoiceTotalAmountWithVat = invoiceTotalAmountWithVat;

        this.amountDueForPayment = amountDueForPayment;
        this.vasId = vasId;
        this.paidAmountToDate = paidAmountToDate;
        this.lastPaymentDate = lastPaymentDate;
        this.lastUpdate = lastUpdate;
        this.paid = paid;

        this.hubId = hubId;
        this.endEntityId = endEntityId;
        this.time = time;
    }

    @Override
    public PersistentState generateMappedObject(MappedSchema schema) {
        if (schema instanceof InvoiceSchemaV1) {
            return new InvoiceSchemaV1.PersistentInvoice(
                competentAuthorityId,
                currentState,
                fileName,
                messageId,
                dateTimeReceipt,
                dateTimeDelivery,
                notificationNotes,
                notificationSignature,
                invoiceHash,
                invoiceOwner,
                invoiceSignature,
                approvedSubject,
                invoiceFormat,
                invoiceNumber,
                invoiceIssueDate,
                invoiceTypeCode,
                invoiceCurrencyCode,
                projectReference,
                purchaseOrderReference,
                tenderOrLotReference,
                sellerVatId,
                sellerTaxRegistrationId,
                buyerVatId,
                buyerTaxRegistrationId,
                buyerTaxRegistrationIdSchemeId,
                buyerElectronicAddress,
                buyerElectronicAddressSchemeId,
                paymentDueDate,
                invoiceTotalAmountWithVat,
                amountDueForPayment,
                vasId,
                paidAmountToDate,
                lastPaymentDate,
                lastUpdate,
                paid,
                linearId.toString(),
                hubId,
                endEntityId,
                time
            );
        }

        throw new IllegalArgumentException("Unrecognized schema $schema");
    }

    @Override
    public Iterable<MappedSchema> supportedSchemas() {
        return ImmutableList.of(new InvoiceSchemaV1());
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return linearId;
    }

    @Override
    public int getFractionDigits() {
        return fractionDigits;
    }

    @NotNull
    @Override
    public List<Party> getMaintainers() {
        return ImmutableList.copyOf(maintainers);
    }

    public TokenPointer<InvoiceState> toPointer() {
        return new TokenPointer<>(new LinearPointer<>(linearId, InvoiceState.class), fractionDigits);
    }

    public Party getIssuer() {
        return issuer;
    }

    public long getTime() {
        return time;
    }

    public String getEndEntityId() {
        return endEntityId;
    }

    public String getHubId() {
        return hubId;
    }

    public String getInvoiceHash() {
        return invoiceHash;
    }

    public String getNotificationSignature() {
        return notificationSignature;
    }

    public String getNotificationNotes() {
        return notificationNotes;
    }

    public String getDateTimeDelivery() {
        return dateTimeDelivery;
    }

    public String getDateTimeReceipt() {
        return dateTimeReceipt;
    }

    public String getInvoiceSignature() {
        return invoiceSignature;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getFileName() {
        return fileName;
    }

    public String getCompetentAuthorityId() {
        return competentAuthorityId;
    }

    public String getCurrentState() {
        return currentState;
    }
 
    public String getInvoiceOwner() {
        return invoiceOwner;
    }

    public String getApprovedSubject() {
        return approvedSubject;
    }

    public String getInvoiceFormat() {
        return invoiceFormat;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public String getInvoiceIssueDate() {
        return invoiceIssueDate;
    }

    public String getInvoiceTypeCode() {
        return invoiceTypeCode;
    }

    public String getInvoiceCurrencyCode() {
        return invoiceCurrencyCode;
    }

    public String getProjectReference() {
        return projectReference;
    }

    public String getPurchaseOrderReference() {
        return purchaseOrderReference;
    }

    public String getTenderOrLotReference() {
        return tenderOrLotReference;
    }

    public String getSellerVatId() {
        return sellerVatId;
    }

    public String getSellerTaxRegistrationId() {
        return sellerTaxRegistrationId;
    }

    public String getBuyerVatId() {
        return buyerVatId;
    }

    public String getBuyerTaxRegistrationId() {
        return buyerTaxRegistrationId;
    }

    public String getBuyerTaxRegistrationIdSchemeId() {
        return buyerTaxRegistrationIdSchemeId;
    }

    public String getBuyerElectronicAddress() {
        return buyerElectronicAddress;
    }

    public String getBuyerElectronicAddressSchemeId() {
        return buyerElectronicAddressSchemeId;
    }

    public String getPaymentDueDate() {
        return paymentDueDate;
    }

    public String getInvoiceTotalAmountWithVat() {
        return invoiceTotalAmountWithVat;
    }

    public String getAmountDueForPayment() {
        return amountDueForPayment;
    }

    public String getVasId() {
        return vasId;
    }

    public String getPaidAmountToDate() {
        return paidAmountToDate;
    }

    public String getLastPaymentDate() {
        return lastPaymentDate;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public String getPaid() {
        return paid;
    }

}