package com.github.devx.routing.datasource.routing.jdbc;

import com.github.devx.routing.datasource.routing.DataSourceMode;
import com.github.devx.routing.datasource.routing.DataSourceWrapper;
import com.github.devx.routing.datasource.routing.RoutingContext;
import com.github.devx.routing.datasource.routing.RoutingContextClearable;
import com.github.devx.routing.datasource.routing.RoutingDataSource;
import lombok.Getter;

import javax.sql.DataSource;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * @author he peng
 * @since 1.0
 */
public class RoutingConnection extends AbstractConnectionAdapter implements RoutingContextClearable {

    private volatile Connection connection;

    @Getter
    private volatile DataSourceMode dataSourceMode;

    private volatile Boolean autoCommit;

    private volatile boolean closed = false;

    private volatile Boolean readOnly;

    private volatile Integer isolation;

    private volatile String schema;

    private final Map<String , String> clientInfoMap = new HashMap<>();

    private Properties clientInfo;

    private final RoutingDataSource routingDataSource;

    public RoutingConnection(RoutingDataSource routingDataSource) {
        this.routingDataSource = routingDataSource;
    }

    public Connection getDelegateConnection() {
        return this.connection;
    }

    @Override
    public Statement createStatement() throws SQLException {
        return new RoutingStatement(routingDataSource , this);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return new RoutingStatement(routingDataSource, this , null , resultSetConcurrency , null);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return new RoutingStatement(routingDataSource, this , resultSetType , resultSetConcurrency , resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return newPreparedStatement(preparedConnection(sql).prepareStatement(sql));
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return newPreparedStatement(preparedConnection(sql).prepareStatement(sql , resultSetType , resultSetConcurrency));
    }


    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return newPreparedStatement(preparedConnection(sql).prepareStatement(sql , resultSetType , resultSetConcurrency , resultSetHoldability));
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return newPreparedStatement(preparedConnection(sql).prepareStatement(sql , autoGeneratedKeys));
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return newPreparedStatement(preparedConnection(sql).prepareStatement(sql , columnIndexes));
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return newPreparedStatement(preparedConnection(sql).prepareStatement(sql , columnNames));
    }

    @Override
    public synchronized void setAutoCommit(boolean autoCommit) throws SQLException {
        if (Objects.nonNull(connection)) {
            connection.setAutoCommit(autoCommit);
        }
        this.autoCommit = autoCommit;
        if (!autoCommit) {
            RoutingContext.setInTx();
        }
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return Objects.nonNull(connection) ? connection.getAutoCommit() : this.autoCommit;
    }

    @Override
    public void commit() throws SQLException {
        try {
            if (Objects.nonNull(connection)) {
                connection.commit();
            }
        } finally {
            forceClear();
        }
    }

    @Override
    public void rollback() throws SQLException {
        try {
            if (Objects.nonNull(connection)) {
                connection.rollback();
            }
        } finally {
            forceClear();
        }
    }

    @Override
    public synchronized void close() throws SQLException {
        try {
            if (Objects.nonNull(connection)) {
                connection.close();
            }
            this.closed = true;
        } finally {
            forceClear();
        }
    }

    @Override
    public boolean isClosed() throws SQLException {
        return Objects.nonNull(connection) ? connection.isClosed() : this.closed;
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return acquireConnection(routingDataSource.getWriteDataSource()).getMetaData();
    }

    @Override
    public synchronized void setReadOnly(boolean readOnly) throws SQLException {
        if (Objects.nonNull(connection)) {
            connection.setReadOnly(readOnly);
        }
        this.readOnly = readOnly;
        RoutingContext.setTxReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return Objects.nonNull(connection) ? connection.isReadOnly() : this.readOnly;
    }

    @Override
    public synchronized void setTransactionIsolation(int level) throws SQLException {
        if (Objects.nonNull(connection)) {
            connection.setTransactionIsolation(level);
        }
        this.isolation = level;
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return Objects.nonNull(connection) ? connection.getTransactionIsolation() : this.isolation;
    }


    @Override
    public Savepoint setSavepoint() throws SQLException {
        return Objects.nonNull(connection) ? connection.setSavepoint() : null;
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return Objects.nonNull(connection) ? connection.setSavepoint(name) : null;
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        if (Objects.nonNull(connection)) {
            connection.rollback(savepoint);
        }
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        if (Objects.nonNull(connection)) {
            connection.releaseSavepoint(savepoint);
        }
    }

    @Override
    public Clob createClob() throws SQLException {
        return Objects.nonNull(connection) ? connection.createClob() : null;
    }

    @Override
    public Blob createBlob() throws SQLException {
        return Objects.nonNull(connection) ? connection.createBlob() : null;
    }

    @Override
    public NClob createNClob() throws SQLException {
        return Objects.nonNull(connection) ? connection.createNClob() : null;
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return Objects.nonNull(connection) ? connection.createSQLXML() : null;
    }


    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return Objects.nonNull(connection) ? connection.createArrayOf(typeName , elements) : null;
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return Objects.nonNull(connection) ? connection.createStruct(typeName , attributes) : null;
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return Objects.nonNull(connection) && connection.isValid(timeout);
    }

    @Override
    public synchronized void setClientInfo(String name, String value) throws SQLClientInfoException {
        if (Objects.nonNull(connection)) {
            connection.setClientInfo(name , value);
        }
        clientInfoMap.put(name , value);
    }

    @Override
    public synchronized void setClientInfo(Properties properties) throws SQLClientInfoException {
        if (Objects.nonNull(connection)) {
            connection.setClientInfo(properties);
        }
        this.clientInfo = properties;
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return Objects.nonNull(connection) ? connection.getClientInfo(name) : null;
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return Objects.nonNull(connection) ? connection.getClientInfo() : null;
    }


    @Override
    public synchronized void setSchema(String schema) throws SQLException {
        if (Objects.nonNull(connection)) {
            connection.setSchema(schema);
        }
        this.schema = schema;
    }

    @Override
    public String getSchema() throws SQLException {
        return Objects.nonNull(connection) ? connection.getSchema() : this.schema;
    }

    private Connection preparedConnection(String sql) throws SQLException {
        if (Objects.nonNull(connection) && RoutingContext.inTx()) {
            return this.connection;
        }
        return acquireConnection(routingDataSource.getDataSourceWithSql(sql));
    }

    private synchronized Connection acquireConnection(DataSource dataSource) throws SQLException {
        if (Objects.nonNull(connection)) {
            this.connection.close();
        }
        this.connection = dataSource.getConnection();
        if (dataSource instanceof DataSourceWrapper) {
            this.dataSourceMode = ((DataSourceWrapper) dataSource).getMode();
        }
        if (this.autoCommit != null) {
            this.connection.setAutoCommit(this.autoCommit);
        }
        if (this.readOnly != null) {
            this.connection.setReadOnly(this.readOnly);
        }
        if (this.isolation != null) {
            this.connection.setTransactionIsolation(this.isolation);
        }
        if (this.schema != null) {
            this.connection.setSchema(this.schema);
        }
        if (this.clientInfo != null) {
            this.connection.setClientInfo(clientInfo);
        }
        for (Map.Entry<String, String> entry : this.clientInfoMap.entrySet()) {
            this.connection.setClientInfo(entry.getKey() , entry.getValue());
        }
        return this.connection;
    }

    private RoutingContextClearPreparedStatement newPreparedStatement(PreparedStatement ps) {
        return new RoutingContextClearPreparedStatement(ps);
    }
}
