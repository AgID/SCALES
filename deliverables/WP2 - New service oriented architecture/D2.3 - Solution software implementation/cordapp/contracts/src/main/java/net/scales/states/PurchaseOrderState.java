package net.scales.states;

import net.scales.contracts.PurchaseOrderContract;
import net.scales.schemas.PurchaseOrderSchemaV1;

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

@BelongsToContract(PurchaseOrderContract.class)
public class PurchaseOrderState extends EvolvableTokenType implements QueryableState {

    private final UniqueIdentifier linearId;
    private final List<Party> maintainers;
    private final Party issuer;
    private final int fractionDigits = 0;

    // Properties of PurchaseOrder
    // Some of these values may evolve over time
    private final String idt;
    private final String receiverId;
    private final String pecMessageId;
    private final String currentState;
    private final String fileName;
    private final String messageId;
    private final String dateTimeReceipt;
    private final String dateTimeDelivery;
    private final String notificationNotes;
    private final String notificationSignature;
    private final String orderHash;
    private final String orderOriginalHash;
    private final String orderSender;
    private final String orderReceiver;
    private final String orderCreationDateAndTime;
    private final String orderProfile;
    private final String orderId;
    private final String orderIssueDate;
    private final String orderTypeCode;
    private final String orderCurrencyCode;
    private final String orderDocumentReference;
    private final String originatorDocumentReference;
    private final String contract;
    private final String accountingCustomerPartyVatId;
    private final String accountingCustomerPartyElectronicAddress;
    private final String accountingCustomerPartyElectronicAddressSchemeId;
    private final String payableAmount;
    private final String approvedSubject;
    private final String buyerVatId;
    private final String buyerElectronicAddress;
    private final String buyerElectronicAddressSchemeId;
    private final String sellerVatId;
    private final String sellerElectronicAddress;
    private final String sellerElectronicAddressSchemeId;
    private final String sellerPartyId;

    private final String hubId;
    private final String endEntityId;
    private final long time;

    public PurchaseOrderState(
        UniqueIdentifier linearId,
        List<Party> maintainers,
        Party issuer,
        String idt,
        String receiverId,
        String pecMessageId,
        String currentState,
        String fileName,
        String messageId,
        String dateTimeReceipt,
        String dateTimeDelivery,
        String notificationNotes,
        String notificationSignature,
        String orderHash,
        String orderOriginalHash,
        String orderSender,
        String orderReceiver,
        String orderCreationDateAndTime,
        String orderProfile,
        String orderId,
        String orderIssueDate,
        String orderTypeCode,
        String orderCurrencyCode,
        String orderDocumentReference,
        String originatorDocumentReference,
        String contract,
        String accountingCustomerPartyVatId,
        String accountingCustomerPartyElectronicAddress,
        String accountingCustomerPartyElectronicAddressSchemeId,
        String payableAmount,
        String approvedSubject,
        String buyerVatId,
        String buyerElectronicAddress,
        String buyerElectronicAddressSchemeId,
        String sellerVatId,
        String sellerElectronicAddress,
        String sellerElectronicAddressSchemeId,
        String sellerPartyId,
        String hubId,
        String endEntityId,
        long time
    ) {
        this.linearId = linearId;
        this.maintainers = maintainers;
        this.issuer = issuer;

        this.idt = idt;
        this.receiverId = receiverId;
        this.pecMessageId = pecMessageId;
        this.currentState = currentState;
        this.fileName = fileName;
        this.messageId = messageId;
        this.dateTimeReceipt = dateTimeReceipt;
        this.dateTimeDelivery = dateTimeDelivery;
        this.notificationNotes = notificationNotes;
        this.notificationSignature = notificationSignature;
        this.orderHash = orderHash;
        this.orderOriginalHash = orderOriginalHash;
        this.orderSender = orderSender;
        this.orderReceiver = orderReceiver;
        this.orderCreationDateAndTime = orderCreationDateAndTime;
        this.orderProfile = orderProfile;
        this.orderId = orderId;
        this.orderIssueDate = orderIssueDate;
        this.orderTypeCode = orderTypeCode;
        this.orderCurrencyCode = orderCurrencyCode;
        this.orderDocumentReference = orderDocumentReference;
        this.originatorDocumentReference = originatorDocumentReference;
        this.contract = contract;
        this.accountingCustomerPartyVatId = accountingCustomerPartyVatId;
        this.accountingCustomerPartyElectronicAddress = accountingCustomerPartyElectronicAddress;
        this.accountingCustomerPartyElectronicAddressSchemeId = accountingCustomerPartyElectronicAddressSchemeId;
        this.payableAmount = payableAmount;
        this.approvedSubject = approvedSubject;
        this.buyerVatId = buyerVatId;
        this.buyerElectronicAddress = buyerElectronicAddress;
        this.buyerElectronicAddressSchemeId = buyerElectronicAddressSchemeId;
        this.sellerVatId = sellerVatId;
        this.sellerElectronicAddress = sellerElectronicAddress;
        this.sellerElectronicAddressSchemeId = sellerElectronicAddressSchemeId;
        this.sellerPartyId = sellerPartyId;

        this.hubId = hubId;
        this.endEntityId = endEntityId;
        this.time = time;
    }

    @Override
    public PersistentState generateMappedObject(MappedSchema schema) {
        if (schema instanceof PurchaseOrderSchemaV1) {
            return new PurchaseOrderSchemaV1.PersistentPurchaseOrder(
                idt,
                receiverId,
                pecMessageId,
                currentState,
                fileName,
                messageId,
                dateTimeReceipt,
                dateTimeDelivery,
                notificationNotes,
                notificationSignature,
                orderHash,
                orderOriginalHash,
                orderSender,
                orderReceiver,
                orderCreationDateAndTime,
                orderProfile,
                orderId,
                orderIssueDate,
                orderTypeCode,
                orderCurrencyCode,
                orderDocumentReference,
                originatorDocumentReference,
                contract,
                accountingCustomerPartyVatId,
                accountingCustomerPartyElectronicAddress,
                accountingCustomerPartyElectronicAddressSchemeId,
                payableAmount,
                approvedSubject,
                buyerVatId,
                buyerElectronicAddress,
                buyerElectronicAddressSchemeId,
                sellerVatId,
                sellerElectronicAddress,
                sellerElectronicAddressSchemeId,
                sellerPartyId,
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
        return ImmutableList.of(new PurchaseOrderSchemaV1());
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

    public TokenPointer<PurchaseOrderState> toPointer() {
        return new TokenPointer<>(new LinearPointer<>(linearId, PurchaseOrderState.class), fractionDigits);
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

    public String getIdt() {
        return idt;
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

    public String getReceiverId() {
        return receiverId;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getFileName() {
        return fileName;
    }

    public String getPecMessageId() {
        return pecMessageId;
    }

    public String getCurrentState() {
        return currentState;
    }
 
    public String getOrderHash() {
        return orderHash;
    }

    public String getApprovedSubject() {
        return approvedSubject;
    }

    public String getOrderOriginalHash() {
        return orderOriginalHash;
    }

    public String getOrderSender() {
        return orderSender;
    }

    public String getOrderReceiver() {
        return orderReceiver;
    }

    public String getOrderCreationDateAndTime() {
        return orderCreationDateAndTime;
    }

    public String getOrderProfile() {
        return orderProfile;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getOrderIssueDate() {
        return orderIssueDate;
    }

    public String getOrderTypeCode() {
        return orderTypeCode;
    }

    public String getOrderCurrencyCode() {
        return orderCurrencyCode;
    }

    public String getSellerVatId() {
        return sellerVatId;
    }

    public String getSellerElectronicAddress() {
        return sellerElectronicAddress;
    }

    public String getSellerElectronicAddressSchemeId() {
        return sellerElectronicAddressSchemeId;
    }

    public String getBuyerVatId() {
        return buyerVatId;
    }

    public String getBuyerElectronicAddress() {
        return buyerElectronicAddress;
    }

    public String getBuyerElectronicAddressSchemeId() {
        return buyerElectronicAddressSchemeId;
    }

    public String getSellerPartyId() {
        return sellerPartyId;
    }

    public String getPayableAmount() {
        return payableAmount;
    }

    public String getOrderDocumentReference() {
        return orderDocumentReference;
    }

    public String getOriginatorDocumentReference() {
        return originatorDocumentReference;
    }

    public String getContract() {
        return contract;
    }

    public String getAccountingCustomerPartyVatId() {
        return accountingCustomerPartyVatId;
    }

    public String getAccountingCustomerPartyElectronicAddress() {
        return accountingCustomerPartyElectronicAddress;
    }

    public String getAccountingCustomerPartyElectronicAddressSchemeId() {
        return accountingCustomerPartyElectronicAddressSchemeId;
    }

}