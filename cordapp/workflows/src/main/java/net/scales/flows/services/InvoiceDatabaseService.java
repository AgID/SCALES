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
        query       += "    INVOICE_NUMBER  VARCHAR(200) PRIMARY KEY,";
        query       += "    INVOICE_FILE    BLOB,";
        query       += "    SDI_FILE        BLOB";
        query       += ")";

        executeUpdate(query, Collections.emptyMap());
    }

    /**
     * Stores the files invoice and SDI notification to database
     */
    public void storeInvoiceFile(String invoiceNumber, InputStream invoiceFile, InputStream sdiFile) throws SQLException {
        String query = "INSERT INTO INVOICE_FILES VALUES (?, ?, ?)";

        Map<Integer, Object> params = new HashMap<Integer, Object>();

        params.put(1, invoiceNumber);
        params.put(2, invoiceFile);
        params.put(3, sdiFile);

        executeUpdate(query, params);
    }

    /**
     * Returns true if invoice has existed by invoice number or invoice hash
     */
    public boolean checkInvoiceExistence(String invoiceNumber, String invoiceHash) throws SQLException {
        String query = "SELECT 1 FROM INVOICE_HASHES WHERE INVOICE_NUMBER = ? OR INVOICE_HASH = ?";

        Map<Integer, Object> params = new HashMap<Integer, Object>();

        params.put(1, invoiceNumber);
        params.put(2, invoiceHash);

        List<Object> rows = executeQuery(query, params);

        if (rows.size() > 0) {
            return true;
        }

        return false;
    }

    /**
     * Gets the invoice file by invoice number
     */
    public Blob getInvoiceFileById(String invoiceNumber) throws SQLException {
        String query = "SELECT INVOICE_FILE FROM INVOICE_FILES WHERE INVOICE_NUMBER = ?";

        Map<Integer, Object> params = new HashMap<Integer, Object>();

        params.put(1, invoiceNumber);

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
        String query = "SELECT  T.TYPE, ";
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
        query       += "        T.LAST_PAYMENT_DATE ";
        query       += "FROM    INVOICE_STATES T ";
        query       += "WHERE   T.END_ENTITY_ID = ? ";
        query       += "AND     T.TIME = (SELECT MAX(TIME) FROM INVOICE_STATES WHERE INVOICE_NUMBER = T.INVOICE_NUMBER) ";
        query       += "ORDER BY T.TIME DESC ";
        query       += "LIMIT ? OFFSET ?";

        Map<Integer, Object> params = new HashMap<Integer, Object>();

        params.put(1, endEntityId);
        params.put(2, pageSize);
        params.put(3, (page - 1) * pageSize);

        return executeQuery(query, params);
    }

    /**
     * Gets the invoice by invoice number
     */
    public Object getInvoiceById(String invoiceNumber) throws SQLException {
        String query = "SELECT  T.TYPE, ";
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
        query       += "        T.LAST_PAYMENT_DATE ";
        query       += "FROM    INVOICE_STATES T ";
        query       += "WHERE   T.INVOICE_NUMBER = ? ";
        query       += "ORDER BY T.TIME DESC ";
        query       += "LIMIT 1";

        Map<Integer, Object> params = new HashMap<Integer, Object>();

        params.put(1, invoiceNumber);

        List<Object> rows = executeQuery(query, params);

        if (rows.size() > 0) {
            return rows.get(0);
        }

        return null;
    }

}