package net.scales.schemas;

import com.google.common.collect.ImmutableList;

import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * A PurchaseOrderState schema
 */
public class PurchaseOrderSchemaV1 extends MappedSchema {

    public PurchaseOrderSchemaV1() {
        super(PurchaseOrderSchema.class, 1, ImmutableList.of(PersistentPurchaseOrder.class));
    }

    @Entity
    @Table(name = "PURCHASE_ORDER_STATES")
    public static class PersistentPurchaseOrder extends PersistentState {

        @Column(name = "ID") private final String id;
        @Column(name = "HUB_ID") private final String hubId;
        @Column(name = "END_ENTITY_ID") private final String endEntityId;
        @Column(name = "TIME") private final long time;

        @Column(name = "IDT") private final String idt;
        @Column(name = "RECEIVER_ID") private final String receiverId;
        @Column(name = "PEC_MESSAGE_ID") private final String pecMessageId;
        @Column(name = "CURRENT_STATE") private final String currentState;
        @Column(name = "FILE_NAME") private final String fileName;
        @Column(name = "MESSAGE_ID") private final String messageId;
        @Column(name = "DATE_TIME_RECEIPT") private final String dateTimeReceipt;
        @Column(name = "DATE_TIME_DELIVERY") private final String dateTimeDelivery;
        @Column(name = "NOTIFICATION_NOTES") private final String notificationNotes;
        @Column(name = "NOTIFICATION_SIGNATURE") private final String notificationSignature;
        @Column(name = "ORDER_HASH") private final String orderHash;
        @Column(name = "ORDER_ORIGINAL_HASH") private final String orderOriginalHash;
        @Column(name = "ORDER_SENDER") private final String orderSender;
        @Column(name = "ORDER_RECEIVER") private final String orderReceiver;
        @Column(name = "ORDER_CREATION_DATE_AND_TIME") private final String orderCreationDateAndTime;
        @Column(name = "ORDER_PROFILE") private final String orderProfile;
        @Column(name = "ORDER_ID") private final String orderId;
        @Column(name = "ORDER_ISSUE_DATE") private final String orderIssueDate;
        @Column(name = "ORDER_TYPE_CODE") private final String orderTypeCode;
        @Column(name = "ORDER_CURRENCY_CODE") private final String orderCurrencyCode;
        @Column(name = "ORDER_DOCUMENT_REFERENCE") private final String orderDocumentReference;
        @Column(name = "ORIGINATOR_DOCUMENT_REFERENCE") private final String originatorDocumentReference;
        @Column(name = "CONTRACT") private final String contract;
        @Column(name = "ACCOUNTING_CUSTOMER_PARTY_VAT_ID") private final String accountingCustomerPartyVatId;
        @Column(name = "ACCOUNTING_CUSTOMER_PARTY_ELECTRONIC_ADDRESS") private final String accountingCustomerPartyElectronicAddress;
        @Column(name = "ACCOUNTING_CUSTOMER_PARTY_ELECTRONIC_ADDRESS_SCHEME_ID") private final String accountingCustomerPartyElectronicAddressSchemeId;
        @Column(name = "PAYABLE_AMOUNT") private final String payableAmount;
        @Column(name = "APPROVED_SUBJECT") private final String approvedSubject;
        @Column(name = "BUYER_VAT_ID") private final String buyerVatId;
        @Column(name = "BUYER_ELECTRONIC_ADDRESS") private final String buyerElectronicAddress;
        @Column(name = "BUYER_ELECTRONIC_ADDRESS_SCHEME_ID") private final String buyerElectronicAddressSchemeId;
        @Column(name = "SELLER_VAT_ID") private final String sellerVatId;
        @Column(name = "SELLER_ELECTRONIC_ADDRESS") private final String sellerElectronicAddress;
        @Column(name = "SELLER_ELECTRONIC_ADDRESS_SCHEME_ID") private final String sellerElectronicAddressSchemeId;
        @Column(name = "SELLER_PARTY_ID") private final String sellerPartyId;

        public PersistentPurchaseOrder(
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
            String id,
            String hubId,
            String endEntityId,
            long time
        ) {
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

            this.id = id;
            this.hubId = hubId;
            this.endEntityId = endEntityId;
            this.time = time;
        }

        /**
         * Default constructor required by hibernate
         */
        public PersistentPurchaseOrder() {
            idt = null;
            receiverId = null;
            pecMessageId = null;
            currentState = null;
            fileName = null;
            messageId = null;
            dateTimeReceipt = null;
            dateTimeDelivery = null;
            notificationNotes = null;
            notificationSignature = null;
            orderHash = null;
            orderOriginalHash = null;
            orderSender = null;
            orderReceiver = null;
            orderCreationDateAndTime = null;
            orderProfile = null;
            orderId = null;
            orderIssueDate = null;
            orderTypeCode = null;
            orderCurrencyCode = null;
            orderDocumentReference = null;
            originatorDocumentReference = null;
            contract = null;
            accountingCustomerPartyVatId = null;
            accountingCustomerPartyElectronicAddress = null;
            accountingCustomerPartyElectronicAddressSchemeId = null;
            payableAmount = null;
            approvedSubject = null;
            buyerVatId = null;
            buyerElectronicAddress = null;
            buyerElectronicAddressSchemeId = null;
            sellerVatId = null;
            sellerElectronicAddress = null;
            sellerElectronicAddressSchemeId = null;
            sellerPartyId = null;

            id = null;
            hubId = null;
            endEntityId = null;
            time = 0;
        }

        public String getId() {
            return id;
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

}