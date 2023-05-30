/*
 *    Copyright 2023 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.github.devx.routing.datasource.routing.jdbc;

import com.github.devx.routing.datasource.routing.RoutingContextClearable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

/**
 * @author he peng
 * @since 1.0
 */
public class RoutingStatement extends AbstractStatementAdapter implements RoutingContextClearable {

    private final RoutingConnection connection;

    private Statement statement;

    private boolean closed = false;

    private Integer maxFieldSize = 0;

    private Integer maxRows;

    private Integer queryTimeout;

    private Integer fetchDirection;

    private Integer fetchSize;

    private Integer resultSetType = ResultSet.TYPE_FORWARD_ONLY;

    private Integer resultSetConcurrency = ResultSet.CONCUR_UPDATABLE;

    private Integer resultSetHoldability = ResultSet.CLOSE_CURSORS_AT_COMMIT;


    public RoutingStatement(RoutingConnection connection ,Integer resultSetType, Integer resultSetConcurrency , Integer resultSetHoldability) {
        this(connection);
        this.resultSetType = resultSetType;
        this.resultSetConcurrency = resultSetConcurrency;
        this.resultSetHoldability = resultSetHoldability;
    }

    public RoutingStatement(RoutingConnection connection) {
        this.connection = connection;
    }

    @Override
    public ResultSet executeQuery(String s) throws SQLException {
        return acquireStatement(s).executeQuery(s);
    }

    @Override
    public int executeUpdate(String s) throws SQLException {
        return acquireStatement(s).executeUpdate(s);
    }

    @Override
    public int executeUpdate(String s, int i) throws SQLException {
        return acquireStatement(s).executeUpdate(s , i);
    }

    @Override
    public int executeUpdate(String s, int[] ints) throws SQLException {
        return acquireStatement(s).executeUpdate(s , ints);
    }

    @Override
    public int executeUpdate(String s, String[] strings) throws SQLException {
        return acquireStatement(s).executeUpdate(s , strings);
    }


    @Override
    public boolean execute(String s) throws SQLException {
        return acquireStatement(s).execute(s);
    }

    @Override
    public boolean execute(String s, int i) throws SQLException {
        return acquireStatement(s).execute(s , i);
    }

    @Override
    public boolean execute(String s, int[] ints) throws SQLException {
        return acquireStatement(s).execute(s , ints);
    }

    @Override
    public boolean execute(String s, String[] strings) throws SQLException {
        return acquireStatement(s).execute(s , strings);
    }

    @Override
    public synchronized void close() throws SQLException {
        try {
            if (Objects.nonNull(statement)) {
                statement.close();
            }
            this.closed = true;
        } finally {
            clearingWithoutTx();
        }
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return maxFieldSize;
    }

    @Override
    public synchronized void setMaxFieldSize(int i) throws SQLException {
        if (Objects.nonNull(statement)) {
            statement.setMaxFieldSize(i);
        }
        this.maxFieldSize = i;
    }

    @Override
    public int getMaxRows() throws SQLException {
        return maxRows;
    }

    @Override
    public synchronized void setMaxRows(int i) throws SQLException {
        if (Objects.nonNull(statement)) {
            statement.setMaxRows(i);
        }
        this.maxRows = i;
    }

    @Override
    public void setEscapeProcessing(boolean b) throws SQLException {
        if (Objects.nonNull(statement)) {
            statement.setEscapeProcessing(b);
        }
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return this.queryTimeout;
    }

    @Override
    public synchronized void setQueryTimeout(int i) throws SQLException {
        if (Objects.nonNull(statement)) {
            statement.setQueryTimeout(i);
        }
        this.queryTimeout = i;
    }

    @Override
    public void cancel() throws SQLException {
        if (Objects.nonNull(statement)) {
            statement.cancel();
        }
    }


    @Override
    public ResultSet getResultSet() throws SQLException {
        return Objects.nonNull(statement) ? statement.getResultSet() : null;
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return Objects.nonNull(statement) ? statement.getUpdateCount() : 0;
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return Objects.nonNull(statement) && statement.getMoreResults();
    }

    @Override
    public synchronized void setFetchDirection(int i) throws SQLException {
        if (Objects.nonNull(statement)) {
            statement.setFetchDirection(i);
        }
        this.fetchDirection = i;
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return this.fetchDirection;
    }

    @Override
    public synchronized void setFetchSize(int i) throws SQLException {
        if (Objects.nonNull(statement)) {
            statement.setFetchSize(i);
        }
        this.fetchSize = i;
    }

    @Override
    public int getFetchSize() throws SQLException {
        return this.fetchSize;
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return Objects.nonNull(statement) ? statement.getResultSetConcurrency() : resultSetConcurrency;
    }

    @Override
    public int getResultSetType() throws SQLException {
        return Objects.nonNull(statement) ? statement.getResultSetType() : resultSetType;
    }

    public void setResultSetType(int i) {
        synchronized (this) {
            this.resultSetType = i;
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.connection;
    }

    @Override
    public boolean getMoreResults(int i) throws SQLException {
        return Objects.nonNull(statement) && statement.getMoreResults();
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return Objects.nonNull(statement) ? statement.getGeneratedKeys() : null;
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return Objects.nonNull(statement) ? statement.getResultSetHoldability() : resultSetHoldability;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return this.closed;
    }

    private synchronized Statement acquireStatement(String sql) throws SQLException {
        this.statement = connection.preparedConnection(sql).createStatement();
        if (this.fetchSize != null) {
            this.statement.setFetchSize(this.fetchSize);
        }
        if (this.fetchDirection != null) {
            this.statement.setFetchDirection(this.fetchDirection);
        }
        if (this.maxRows != null) {
            this.statement.setMaxRows(this.maxRows);
        }
        if (this.maxFieldSize != null) {
            this.statement.setMaxFieldSize(maxFieldSize);
        }
        if (this.queryTimeout != null) {
            this.statement.setQueryTimeout(this.queryTimeout);
        }
        return this.statement;
    }
}
