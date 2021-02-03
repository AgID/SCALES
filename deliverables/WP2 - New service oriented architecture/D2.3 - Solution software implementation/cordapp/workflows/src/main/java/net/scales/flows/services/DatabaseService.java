package net.scales.flows.services;

import net.corda.core.node.ServiceHub;
import net.corda.core.node.services.CordaService;
import net.corda.core.serialization.SingletonSerializeAsToken;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CordaService
public class DatabaseService extends SingletonSerializeAsToken {

    protected final static Logger logger = LoggerFactory.getLogger(DatabaseService.class);

    private final ServiceHub service;

    public DatabaseService(ServiceHub service) {
        this.service = service;
    }

    /**
     * Executes a database query
     *
     * @param query  The query string with blanks for the parameters
     * @param params The parameters to fill the blanks in the query string
     */
    protected List<Object> executeQuery(String query, Map<Integer, Object> params) throws SQLException {
        PreparedStatement statement = prepareStatement(query, params);
        List<Object> rows = new ArrayList<>();

        try {
            ResultSet result = statement.executeQuery();
            int numCols = result.getMetaData().getColumnCount();

            while (result.next()) {
                List<Object> row = new ArrayList<>();

                for (int col = 1; col <= numCols; col++) {
                    row.add(result.getObject(col));
                }

                rows.add(row);
            }

        } catch (SQLException ex) {
            logger.error("", ex);
            throw ex;

        } finally {
            statement.close();
        }

        return rows;
    }

    /**
     * Executes a database update
     *
     * @param query  The query string with blanks for the parameters
     * @param params The parameters to fill the blanks in the query string
     */
    protected void executeUpdate(String query, Map<Integer, Object> params) throws SQLException {
        PreparedStatement statement = prepareStatement(query, params);

        try {
            statement.executeUpdate();

        } catch (SQLException ex) {
            logger.error("", ex);
            throw ex;

        } finally {
            statement.close();
        }
    }

    /**
     * Creates a PreparedStatement - a precompiled SQL statement to be executed
     * against the database
     *
     * @param query  The query string with blanks for the parameters
     * @param params The parameters to fill the blanks in the query string
     *
     * @return The query string and params compiled into a PreparedStatement
     */
    private PreparedStatement prepareStatement(String query, Map<Integer, Object> params) throws SQLException {
        PreparedStatement statement = service.jdbcSession().prepareStatement(query);

        for (int key : params.keySet()) {
            Object value = params.get(key);

            if (value instanceof String) {
                statement.setString(key, (String) value);

            } else if (value instanceof Integer) {
                statement.setInt(key, (int) value);

            } else if (value instanceof Long) {
                statement.setLong(key, (long) value);

            } else if (value instanceof InputStream) {
                statement.setBlob(key, (InputStream) value);

            } else {
                throw new IllegalArgumentException("Unsupported type");
            }
        }

        logger.info(getQueryString(statement));

        return statement;
    }

    /**
     * Get the query string from PreparedStatement
     */
    private String getQueryString(PreparedStatement statement) {
        String sql = statement.toString();

        return sql.substring(sql.indexOf(":") + 2).replaceAll("\\s+", " ");
    }

}