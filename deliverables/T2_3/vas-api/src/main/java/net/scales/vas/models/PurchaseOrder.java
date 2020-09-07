package net.scales.vas.models;

public class PurchaseOrder {

    private final String id;
    private final String hubId;
    private final String endEntityId;

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

    public PurchaseOrder(
        String id,
        String hubId,
        String endEntityId,
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
        String sellerPartyId
    ) {
        this.id = id;
        this.hubId = hubId;
        this.endEntityId = endEntityId;

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
    }

    public String getEndEntityId() {
        return endEntityId;
    }

    public String getHubId() {
        return hubId;
    }

    public String getId() {
        return id;
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