package com.github.devx.routing.datasource.routing.jdbc;

import com.github.devx.routing.datasource.exception.UnsupportedJdbcMethodException;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * @author he peng
 * @since 1.0
 */
public abstract class AbstractUnsupportedConnection extends WrapperAdapter implements Connection {

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        throw new UnsupportedJdbcMethodException("Unsupported jdbc method prepareCall(String sql)");
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        throw new UnsupportedJdbcMethodException("Unsupported jdbc method prepareCall(String sql, int resultSetType, int resultSetConcurrency)");
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        throw new UnsupportedJdbcMethodException("Unsupported jdbc method prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)");
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return sql;
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        throw new UnsupportedJdbcMethodException("Unsupported jdbc method abort(Executor executor)");
    }

    @Override
    public final Map<String, Class<?>> getTypeMap() throws SQLException {
        throw new UnsupportedJdbcMethodException("Unsupported jdbc method getTypeMap()");
    }

    @Override
    public final void setTypeMap(final Map<String, Class<?>> map) throws SQLException {
        throw new UnsupportedJdbcMethodException("Unsupported jdbc method setTypeMap(final Map<String, Class<?>> map)");
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        throw new UnsupportedJdbcMethodException("Unsupported jdbc method getNetworkTimeout()");
    }

    @Override
    public final void setNetworkTimeout(final Executor executor, final int milliseconds) throws SQLException {
        throw new UnsupportedJdbcMethodException("Unsupported jdbc method getNetworkTimeout(final Executor executor, final int milliseconds)");
    }


}
