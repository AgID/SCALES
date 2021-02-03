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
public class PurchaseOrderDatabaseService extends DatabaseService {

    public PurchaseOrderDatabaseService(ServiceHub service) throws SQLException {
        super(service);

        createTables();
    }

    /**
     * Creates the tables
     */
    private void createTables() throws SQLException {
        String query = "CREATE TABLE IF NOT EXISTS PURCHASE_ORDER_FILES (";
        query       += "    ID                  VARCHAR(200) PRIMARY KEY,";
        query       += "    ORDER_FILE          BLOB,";
        query       += "    NOTIFICATION_FILE   BLOB";
        query       += ")";

        executeUpdate(query, Collections.emptyMap());
    }

    /**
     * Stores the files order and notification to database
     */
    public void insertPurchaseOrderFile(String id, InputStream order, InputStream notification) throws SQLException {
        String query = "INSERT INTO PURCHASE_ORDER_FILES VALUES (?, ?, ?)";

        Map<Integer, Object> params = new HashMap<Integer, Object>();

        params.put(1, id);
        params.put(2, order);
        params.put(3, notification);

        executeUpdate(query, params);
    }

    /**
     * Gets the order file by id
     */
    public Blob getPurchaseOrderFileById(String id) throws SQLException {
        String query = "SELECT ORDER_FILE FROM PURCHASE_ORDER_FILES WHERE ID = ?";

        Map<Integer, Object> params = new HashMap<Integer, Object>();

        params.put(1, id);

        List<Object> rows = executeQuery(query, params);

        if (rows.size() > 0) {
            return (Blob) ((List<Object>) rows.get(0)).get(0);
        }

        return null;
    }

    /**
     * Gets the order list by end entity id
     */
    public List<Object> getPurchaseOrderListByEndEntityId(String endEntityId, int page, int pageSize) throws SQLException {
        String query = "SELECT  T.ID, ";
        query       += "        T.HUB_ID, ";
        query       += "        T.END_ENTITY_ID, ";
        query       += "        T.IDT, ";
        query       += "        T.RECEIVER_ID, ";
        query       += "        T.PEC_MESSAGE_ID, ";
        query       += "        T.CURRENT_STATE, ";
        query       += "        T.FILE_NAME, ";
        query       += "        T.MESSAGE_ID, ";
        query       += "        T.DATE_TIME_RECEIPT, ";
        query       += "        T.DATE_TIME_DELIVERY, ";
        query       += "        T.NOTIFICATION_NOTES, ";
        query       += "        T.NOTIFICATION_SIGNATURE, ";
        query       += "        T.ORDER_HASH, ";
        query       += "        T.ORDER_ORIGINAL_HASH, ";
        query       += "        T.ORDER_SENDER, ";
        query       += "        T.ORDER_RECEIVER, ";
        query       += "        T.ORDER_CREATION_DATE_AND_TIME, ";
        query       += "        T.ORDER_PROFILE, ";
        query       += "        T.ORDER_ID, ";
        query       += "        T.ORDER_ISSUE_DATE, ";
        query       += "        T.ORDER_TYPE_CODE, ";
        query       += "        T.ORDER_CURRENCY_CODE, ";
        query       += "        T.ORDER_DOCUMENT_REFERENCE, ";
        query       += "        T.ORIGINATOR_DOCUMENT_REFERENCE, ";
        query       += "        T.CONTRACT, ";
        query       += "        T.ACCOUNTING_CUSTOMER_PARTY_VAT_ID, ";
        query       += "        T.ACCOUNTING_CUSTOMER_PARTY_ELECTRONIC_ADDRESS, ";
        query       += "        T.ACCOUNTING_CUSTOMER_PARTY_ELECTRONIC_ADDRESS_SCHEME_ID, ";
        query       += "        T.PAYABLE_AMOUNT, ";
        query       += "        T.APPROVED_SUBJECT, ";
        query       += "        T.BUYER_VAT_ID, ";
        query       += "        T.BUYER_ELECTRONIC_ADDRESS, ";
        query       += "        T.BUYER_ELECTRONIC_ADDRESS_SCHEME_ID, ";
        query       += "        T.SELLER_VAT_ID, ";
        query       += "        T.SELLER_ELECTRONIC_ADDRESS, ";
        query       += "        T.SELLER_ELECTRONIC_ADDRESS_SCHEME_ID, ";
        query       += "        T.SELLER_PARTY_ID ";
        query       += "FROM    PURCHASE_ORDER_STATES T ";
        query       += "WHERE   (T.BUYER_VAT_ID = ? OR T.SELLER_VAT_ID = ?) ";
        query       += "AND     T.TIME = (SELECT MAX(TIME) FROM PURCHASE_ORDER_STATES WHERE ID = T.ID) ";
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
     * Gets the order list by filter
     */
    public List<Object> getPurchaseOrderList(String sellerVatId, String buyerVatId, int page, int pageSize) throws SQLException {
        String query = "SELECT  T.ID, ";
        query       += "        T.HUB_ID, ";
        query       += "        T.END_ENTITY_ID, ";
        query       += "        T.IDT, ";
        query       += "        T.RECEIVER_ID, ";
        query       += "        T.PEC_MESSAGE_ID, ";
        query       += "        T.CURRENT_STATE, ";
        query       += "        T.FILE_NAME, ";
        query       += "        T.MESSAGE_ID, ";
        query       += "        T.DATE_TIME_RECEIPT, ";
        query       += "        T.DATE_TIME_DELIVERY, ";
        query       += "        T.NOTIFICATION_NOTES, ";
        query       += "        T.NOTIFICATION_SIGNATURE, ";
        query       += "        T.ORDER_HASH, ";
        query       += "        T.ORDER_ORIGINAL_HASH, ";
        query       += "        T.ORDER_SENDER, ";
        query       += "        T.ORDER_RECEIVER, ";
        query       += "        T.ORDER_CREATION_DATE_AND_TIME, ";
        query       += "        T.ORDER_PROFILE, ";
        query       += "        T.ORDER_ID, ";
        query       += "        T.ORDER_ISSUE_DATE, ";
        query       += "        T.ORDER_TYPE_CODE, ";
        query       += "        T.ORDER_CURRENCY_CODE, ";
        query       += "        T.ORDER_DOCUMENT_REFERENCE, ";
        query       += "        T.ORIGINATOR_DOCUMENT_REFERENCE, ";
        query       += "        T.CONTRACT, ";
        query       += "        T.ACCOUNTING_CUSTOMER_PARTY_VAT_ID, ";
        query       += "        T.ACCOUNTING_CUSTOMER_PARTY_ELECTRONIC_ADDRESS, ";
        query       += "        T.ACCOUNTING_CUSTOMER_PARTY_ELECTRONIC_ADDRESS_SCHEME_ID, ";
        query       += "        T.PAYABLE_AMOUNT, ";
        query       += "        T.APPROVED_SUBJECT, ";
        query       += "        T.BUYER_VAT_ID, ";
        query       += "        T.BUYER_ELECTRONIC_ADDRESS, ";
        query       += "        T.BUYER_ELECTRONIC_ADDRESS_SCHEME_ID, ";
        query       += "        T.SELLER_VAT_ID, ";
        query       += "        T.SELLER_ELECTRONIC_ADDRESS, ";
        query       += "        T.SELLER_ELECTRONIC_ADDRESS_SCHEME_ID, ";
        query       += "        T.SELLER_PARTY_ID ";
        query       += "FROM    PURCHASE_ORDER_STATES T ";
        query       += "WHERE   (? = '' OR (? <> '' AND T.SELLER_VAT_ID = ?)) ";
        query       += "AND     (? = '' OR (? <> '' AND T.BUYER_VAT_ID = ?)) ";
        query       += "AND     T.TIME = (SELECT MAX(TIME) FROM PURCHASE_ORDER_STATES WHERE ID = T.ID) ";
        query       += "ORDER BY T.TIME DESC ";
        query       += "LIMIT ? OFFSET ?";

        Map<Integer, Object> params = new HashMap<Integer, Object>();

        params.put(1, sellerVatId);
        params.put(2, sellerVatId);
        params.put(3, sellerVatId);
        params.put(4, buyerVatId);
        params.put(5, buyerVatId);
        params.put(6, buyerVatId);
        params.put(7, pageSize);
        params.put(8, (page - 1) * pageSize);

        return executeQuery(query, params);
    }

    /**
     * Gets the order by id
     */
    public Object getPurchaseOrderById(String id) throws SQLException {
        String query = "SELECT  T.ID, ";
        query       += "        T.HUB_ID, ";
        query       += "        T.END_ENTITY_ID, ";
        query       += "        T.IDT, ";
        query       += "        T.RECEIVER_ID, ";
        query       += "        T.PEC_MESSAGE_ID, ";
        query       += "        T.CURRENT_STATE, ";
        query       += "        T.FILE_NAME, ";
        query       += "        T.MESSAGE_ID, ";
        query       += "        T.DATE_TIME_RECEIPT, ";
        query       += "        T.DATE_TIME_DELIVERY, ";
        query       += "        T.NOTIFICATION_NOTES, ";
        query       += "        T.NOTIFICATION_SIGNATURE, ";
        query       += "        T.ORDER_HASH, ";
        query       += "        T.ORDER_ORIGINAL_HASH, ";
        query       += "        T.ORDER_SENDER, ";
        query       += "        T.ORDER_RECEIVER, ";
        query       += "        T.ORDER_CREATION_DATE_AND_TIME, ";
        query       += "        T.ORDER_PROFILE, ";
        query       += "        T.ORDER_ID, ";
        query       += "        T.ORDER_ISSUE_DATE, ";
        query       += "        T.ORDER_TYPE_CODE, ";
        query       += "        T.ORDER_CURRENCY_CODE, ";
        query       += "        T.ORDER_DOCUMENT_REFERENCE, ";
        query       += "        T.ORIGINATOR_DOCUMENT_REFERENCE, ";
        query       += "        T.CONTRACT, ";
        query       += "        T.ACCOUNTING_CUSTOMER_PARTY_VAT_ID, ";
        query       += "        T.ACCOUNTING_CUSTOMER_PARTY_ELECTRONIC_ADDRESS, ";
        query       += "        T.ACCOUNTING_CUSTOMER_PARTY_ELECTRONIC_ADDRESS_SCHEME_ID, ";
        query       += "        T.PAYABLE_AMOUNT, ";
        query       += "        T.APPROVED_SUBJECT, ";
        query       += "        T.BUYER_VAT_ID, ";
        query       += "        T.BUYER_ELECTRONIC_ADDRESS, ";
        query       += "        T.BUYER_ELECTRONIC_ADDRESS_SCHEME_ID, ";
        query       += "        T.SELLER_VAT_ID, ";
        query       += "        T.SELLER_ELECTRONIC_ADDRESS, ";
        query       += "        T.SELLER_ELECTRONIC_ADDRESS_SCHEME_ID, ";
        query       += "        T.SELLER_PARTY_ID ";
        query       += "FROM    PURCHASE_ORDER_STATES T ";
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