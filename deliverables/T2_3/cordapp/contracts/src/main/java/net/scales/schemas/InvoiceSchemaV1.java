package net.scales.schemas;

import com.google.common.collect.ImmutableList;

import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * An InvoiceState schema
 */
public class InvoiceSchemaV1 extends MappedSchema {

    public InvoiceSchemaV1() {
        super(InvoiceSchema.class, 1, ImmutableList.of(PersistentInvoice.class));
    }

    @Entity
    @Table(name = "INVOICE_STATES")
    public static class PersistentInvoice extends PersistentState {

        @Column(name = "ID") private final String id;
        @Column(name = "HUB_ID") private final String hubId;
        @Column(name = "END_ENTITY_ID") private final String endEntityId;
        @Column(name = "TIME") private final long time;

        @Column(name = "COMPETENT_AUTHORITY_ID") private final String competentAuthorityId;
        @Column(name = "CURRENT_STATE") private final String currentState;
        @Column(name = "FILE_NAME") private final String fileName;
        @Column(name = "MESSAGE_ID") private final String messageId;
        @Column(name = "DATE_TIME_RECEIPT") private final String dateTimeReceipt;
        @Column(name = "DATE_TIME_DELIVERY") private final String dateTimeDelivery;
        @Column(name = "NOTIFICATION_NOTES") private final String notificationNotes;
        @Column(name = "NOTIFICATION_SIGNATURE") private final String notificationSignature;
        @Column(name = "INVOICE_HASH") private final String invoiceHash;
        @Column(name = "INVOICE_OWNER") private final String invoiceOwner;
        @Column(name = "INVOICE_SIGNATURE") private final String invoiceSignature;
        @Column(name = "APPROVED_SUBJECT") private final String approvedSubject;
        @Column(name = "INVOICE_FORMAT") private final String invoiceFormat;
        @Column(name = "INVOICE_NUMBER") private final String invoiceNumber;
        @Column(name = "INVOICE_ISSUE_DATE") private final String invoiceIssueDate;
        @Column(name = "INVOICE_TYPE_CODE") private final String invoiceTypeCode;
        @Column(name = "INVOICE_CURRENCY_CODE") private final String invoiceCurrencyCode;
        @Column(name = "PROJECT_REFERENCE") private final String projectReference;
        @Column(name = "PURCHASE_ORDER_REFERENCE") private final String purchaseOrderReference;
        @Column(name = "TENDER_OR_LOT_REFERENCE") private final String tenderOrLotReference;
        @Column(name = "SELLER_VAT_ID") private final String sellerVatId;
        @Column(name = "SELLER_TAX_REGISTRATION_ID") private final String sellerTaxRegistrationId;
        @Column(name = "BUYER_VAT_ID") private final String buyerVatId;
        @Column(name = "BUYER_TAX_REGISTRATION_ID") private final String buyerTaxRegistrationId;
        @Column(name = "BUYER_TAX_REGISTRATION_ID_SCHEME_ID") private final String buyerTaxRegistrationIdSchemeId;
        @Column(name = "BUYER_ELECTRONIC_ADDRESS") private final String buyerElectronicAddress;
        @Column(name = "BUYER_ELECTRONIC_ADDRESS_SCHEME_ID") private final String buyerElectronicAddressSchemeId;
        @Column(name = "PAYMENT_DUE_DATE") private final String paymentDueDate;
        @Column(name = "INVOICE_TOTAL_AMOUNT_WITH_VAT") private final String invoiceTotalAmountWithVat;

        @Column(name = "AMOUNT_DUE_FOR_PAYMENT") private final String amountDueForPayment;
        @Column(name = "VAS_ID") private final String vasId;
        @Column(name = "PAID_AMOUNT_TO_DATE") private final String paidAmountToDate;
        @Column(name = "LAST_PAYMENT_DATE") private final String lastPaymentDate;
        @Column(name = "LAST_UPDATE") private final String lastUpdate;
        @Column(name = "PAID") private final String paid;

        public PersistentInvoice(
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
            String id,
            String hubId,
            String endEntityId,
            long time
        ) {
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

            this.id = id;
            this.hubId = hubId;
            this.endEntityId = endEntityId;
            this.time = time;
        }

        /**
         * Default constructor required by hibernate
         */
        public PersistentInvoice() {
            competentAuthorityId = null;
            currentState = null;
            fileName = null;
            messageId = null;
            dateTimeReceipt = null;
            dateTimeDelivery = null;
            notificationNotes = null;
            notificationSignature = null;
            invoiceHash = null;
            invoiceOwner = null;
            invoiceSignature = null;
            approvedSubject = null;
            invoiceFormat = null;
            invoiceNumber = null;
            invoiceIssueDate = null;
            invoiceTypeCode = null;
            invoiceCurrencyCode = null;
            projectReference = null;
            purchaseOrderReference = null;
            tenderOrLotReference = null;
            sellerVatId = null;
            sellerTaxRegistrationId = null;
            buyerVatId = null;
            buyerTaxRegistrationId = null;
            buyerTaxRegistrationIdSchemeId = null;
            buyerElectronicAddress = null;
            buyerElectronicAddressSchemeId = null;
            paymentDueDate = null;
            invoiceTotalAmountWithVat = null;

            amountDueForPayment = null;
            vasId = null;
            paidAmountToDate = null;
            lastPaymentDate = null;
            lastUpdate = null;
            paid = null;

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

}