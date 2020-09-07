package net.scales.flows.services;

import net.corda.core.node.ServiceHub;
import net.corda.core.node.services.CordaService;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CordaService
public class InvoiceDatabaseService extends DatabaseService {

    public InvoiceDatabaseService(ServiceHub service) throws SQLException {
        super(service);

        createTables();
    }

    /**
     * Creates the tables
     */
    private void createTables() throws SQLException {
        String query = "CREATE TABLE IF NOT EXISTS INVOICE_FILES (";
        query       += "    ID                  VARCHAR(200) PRIMARY KEY,";
        query       += "    INVOICE_FILE        BLOB,";
        query       += "    NOTIFICATION_FILE   BLOB";
        query       += ")";

        executeUpdate(query, Collections.emptyMap());
    }

    /**
     * Stores the files invoice and notification to database
     */
    public void insertInvoiceFile(String id, InputStream invoice, InputStream notification) throws SQLException {
        String query = "INSERT INTO INVOICE_FILES VALUES (?, ?, ?)";

        Map<Integer, Object> params = new HashMap<Integer, Object>();

        params.put(1, id);
        params.put(2, invoice);
        params.put(3, notification);

        executeUpdate(query, params);
    }

    /**
     * Gets the invoice file by id
     */
    public Blob getInvoiceFileById(String id) throws SQLException {
        String query = "SELECT INVOICE_FILE FROM INVOICE_FILES WHERE ID = ?";

        Map<Integer, Object> params = new HashMap<Integer, Object>();

        params.put(1, id);

        List<Object> rows = executeQuery(query, params);

        if (rows.size() > 0) {
            return (Blob) ((List<Object>) rows.get(0)).get(0);
        }

        return null;
    }

    /**
     * Gets the invoice list by end entity id
     */
    public List<Object> getInvoiceListByEndEntityId(String endEntityId, int page, int pageSize) throws SQLException {
        String query = "SELECT  T.ID, ";
        query       += "        T.HUB_ID, ";
        query       += "        T.END_ENTITY_ID, ";
        query       += "        T.COMPETENT_AUTHORITY_ID, ";
        query       += "        T.CURRENT_STATE, ";
        query       += "        T.FILE_NAME, ";
        query       += "        T.MESSAGE_ID, ";
        query       += "        T.DATE_TIME_RECEIPT, ";
        query       += "        T.DATE_TIME_DELIVERY, ";
        query       += "        T.NOTIFICATION_NOTES, ";
        query       += "        T.NOTIFICATION_SIGNATURE, ";
        query       += "        T.INVOICE_HASH, ";
        query       += "        T.INVOICE_OWNER, ";
        query       += "        T.INVOICE_SIGNATURE, ";
        query       += "        T.APPROVED_SUBJECT, ";
        query       += "        T.INVOICE_FORMAT, ";
        query       += "        T.INVOICE_NUMBER, ";
        query       += "        T.INVOICE_ISSUE_DATE, ";
        query       += "        T.INVOICE_TYPE_CODE, ";
        query       += "        T.INVOICE_CURRENCY_CODE, ";
        query       += "        T.PROJECT_REFERENCE, ";
        query       += "        T.PURCHASE_ORDER_REFERENCE, ";
        query       += "        T.TENDER_OR_LOT_REFERENCE, ";
        query       += "        T.SELLER_VAT_ID, ";
        query       += "        T.SELLER_TAX_REGISTRATION_ID, ";
        query       += "        T.BUYER_VAT_ID, ";
        query       += "        T.BUYER_TAX_REGISTRATION_ID, ";
        query       += "        T.BUYER_TAX_REGISTRATION_ID_SCHEME_ID, ";
        query       += "        T.BUYER_ELECTRONIC_ADDRESS, ";
        query       += "        T.BUYER_ELECTRONIC_ADDRESS_SCHEME_ID, ";
        query       += "        T.PAYMENT_DUE_DATE, ";
        query       += "        T.INVOICE_TOTAL_AMOUNT_WITH_VAT, ";
        query       += "        T.AMOUNT_DUE_FOR_PAYMENT, ";
        query       += "        T.VAS_ID, ";
        query       += "        T.PAID_AMOUNT_TO_DATE, ";
        query       += "        T.LAST_PAYMENT_DATE, ";
        query       += "        T.LAST_UPDATE, ";
        query       += "        T.PAID ";
        query       += "FROM    INVOICE_STATES T ";
        query       += "WHERE   (T.BUYER_VAT_ID = ? OR T.SELLER_VAT_ID = ?) ";
        query       += "AND     T.TIME = (SELECT MAX(TIME) FROM INVOICE_STATES WHERE ID = T.ID) ";
        query       += "ORDER BY T.TIME DESC ";
        query       += "LIMIT ? OFFSET ?";

        Map<Integer, Object> params = new HashMap<Integer, Object>();

        params.put(1, endEntityId);
        params.put(2, endEntityId);
        params.put(3, pageSize);
        params.put(4, (page - 1) * pageSize);

        return executeQuery(query, params);
    }

    /**
     * Gets the invoice list by filter
     */
    public List<Object> getInvoiceList(String invoiceNumber, String invoiceTypeCode, String invoiceIssueDate, String sellerVatId, String buyerVatId, int page, int pageSize) throws SQLException {
        String query = "SELECT  T.ID, ";
        query       += "        T.HUB_ID, ";
        query       += "        T.END_ENTITY_ID, ";
        query       += "        T.COMPETENT_AUTHORITY_ID, ";
        query       += "        T.CURRENT_STATE, ";
        query       += "        T.FILE_NAME, ";
        query       += "        T.MESSAGE_ID, ";
        query       += "        T.DATE_TIME_RECEIPT, ";
        query       += "        T.DATE_TIME_DELIVERY, ";
        query       += "        T.NOTIFICATION_NOTES, ";
        query       += "        T.NOTIFICATION_SIGNATURE, ";
        query       += "        T.INVOICE_HASH, ";
        query       += "        T.INVOICE_OWNER, ";
        query       += "        T.INVOICE_SIGNATURE, ";
        query       += "        T.APPROVED_SUBJECT, ";
        query       += "        T.INVOICE_FORMAT, ";
        query       += "        T.INVOICE_NUMBER, ";
        query       += "        T.INVOICE_ISSUE_DATE, ";
        query       += "        T.INVOICE_TYPE_CODE, ";
        query       += "        T.INVOICE_CURRENCY_CODE, ";
        query       += "        T.PROJECT_REFERENCE, ";
        query       += "        T.PURCHASE_ORDER_REFERENCE, ";
        query       += "        T.TENDER_OR_LOT_REFERENCE, ";
        query       += "        T.SELLER_VAT_ID, ";
        query       += "        T.SELLER_TAX_REGISTRATION_ID, ";
        query       += "        T.BUYER_VAT_ID, ";
        query       += "        T.BUYER_TAX_REGISTRATION_ID, ";
        query       += "        T.BUYER_TAX_REGISTRATION_ID_SCHEME_ID, ";
        query       += "        T.BUYER_ELECTRONIC_ADDRESS, ";
        query       += "        T.BUYER_ELECTRONIC_ADDRESS_SCHEME_ID, ";
        query       += "        T.PAYMENT_DUE_DATE, ";
        query       += "        T.INVOICE_TOTAL_AMOUNT_WITH_VAT, ";
        query       += "        T.AMOUNT_DUE_FOR_PAYMENT, ";
        query       += "        T.VAS_ID, ";
        query       += "        T.PAID_AMOUNT_TO_DATE, ";
        query       += "        T.LAST_PAYMENT_DATE, ";
        query       += "        T.LAST_UPDATE, ";
        query       += "        T.PAID ";
        query       += "FROM    INVOICE_STATES T ";
        query       += "WHERE   (? = '' OR (? <> '' AND T.INVOICE_NUMBER = ?)) ";
        query       += "AND     (? = '' OR (? <> '' AND T.INVOICE_TYPE_CODE = ?)) ";
        query       += "AND     (? = '' OR (? <> '' AND T.INVOICE_ISSUE_DATE = ?)) ";
        query       += "AND     (? = '' OR (? <> '' AND T.SELLER_VAT_ID = ?)) ";
        query       += "AND     (? = '' OR (? <> '' AND T.BUYER_VAT_ID = ?)) ";
        query       += "AND     T.TIME = (SELECT MAX(TIME) FROM INVOICE_STATES WHERE ID = T.ID) ";
        query       += "ORDER BY T.TIME DESC ";
        query       += "LIMIT ? OFFSET ?";

        Map<Integer, Object> params = new HashMap<Integer, Object>();

        params.put(1, invoiceNumber);
        params.put(2, invoiceNumber);
        params.put(3, invoiceNumber);
        params.put(4, invoiceTypeCode);
        params.put(5, invoiceTypeCode);
        params.put(6, invoiceTypeCode);
        params.put(7, invoiceIssueDate);
        params.put(8, invoiceIssueDate);
        params.put(9, invoiceIssueDate);
        params.put(10, sellerVatId);
        params.put(11, sellerVatId);
        params.put(12, sellerVatId);
        params.put(13, buyerVatId);
        params.put(14, buyerVatId);
        params.put(15, buyerVatId);
        params.put(16, pageSize);
        params.put(17, (page - 1) * pageSize);

        return executeQuery(query, params);
    }

    /**
     * Gets the invoice by id
     */
    public Object getInvoiceById(String id) throws SQLException {
        String query = "SELECT  T.ID, ";
        query       += "        T.HUB_ID, ";
        query       += "        T.END_ENTITY_ID, ";
        query       += "        T.COMPETENT_AUTHORITY_ID, ";
        query       += "        T.CURRENT_STATE, ";
        query       += "        T.FILE_NAME, ";
        query       += "        T.MESSAGE_ID, ";
        query       += "        T.DATE_TIME_RECEIPT, ";
        query       += "        T.DATE_TIME_DELIVERY, ";
        query       += "        T.NOTIFICATION_NOTES, ";
        query       += "        T.NOTIFICATION_SIGNATURE, ";
        query       += "        T.INVOICE_HASH, ";
        query       += "        T.INVOICE_OWNER, ";
        query       += "        T.INVOICE_SIGNATURE, ";
        query       += "        T.APPROVED_SUBJECT, ";
        query       += "        T.INVOICE_FORMAT, ";
        query       += "        T.INVOICE_NUMBER, ";
        query       += "        T.INVOICE_ISSUE_DATE, ";
        query       += "        T.INVOICE_TYPE_CODE, ";
        query       += "        T.INVOICE_CURRENCY_CODE, ";
        query       += "        T.PROJECT_REFERENCE, ";
        query       += "        T.PURCHASE_ORDER_REFERENCE, ";
        query       += "        T.TENDER_OR_LOT_REFERENCE, ";
        query       += "        T.SELLER_VAT_ID, ";
        query       += "        T.SELLER_TAX_REGISTRATION_ID, ";
        query       += "        T.BUYER_VAT_ID, ";
        query       += "        T.BUYER_TAX_REGISTRATION_ID, ";
        query       += "        T.BUYER_TAX_REGISTRATION_ID_SCHEME_ID, ";
        query       += "        T.BUYER_ELECTRONIC_ADDRESS, ";
        query       += "        T.BUYER_ELECTRONIC_ADDRESS_SCHEME_ID, ";
        query       += "        T.PAYMENT_DUE_DATE, ";
        query       += "        T.INVOICE_TOTAL_AMOUNT_WITH_VAT, ";
        query       += "        T.AMOUNT_DUE_FOR_PAYMENT, ";
        query       += "        T.VAS_ID, ";
        query       += "        T.PAID_AMOUNT_TO_DATE, ";
        query       += "        T.LAST_PAYMENT_DATE, ";
        query       += "        T.LAST_UPDATE, ";
        query       += "        T.PAID ";
        query       += "FROM    INVOICE_STATES T ";
        query       += "WHERE   T.ID = ? ";
        query       += "ORDER BY T.TIME DESC ";
        query       += "LIMIT 1";

        Map<Integer, Object> params = new HashMap<Integer, Object>();

        params.put(1, id);

        List<Object> rows = executeQuery(query, params);

        if (rows.size() > 0) {
            return rows.get(0);
        }

        return null;
    }

}