package com.github.devx.routing.datasource.routing.jdbc;

import com.github.devx.routing.datasource.exception.UnsupportedJdbcMethodException;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author he peng
 * @since 1.0
 */
public abstract class AbstractUnsupportedStatement extends WrapperAdapter implements Statement {

    @Override
    public final void addBatch(final String sql) throws SQLException {
        throw new UnsupportedJdbcMethodException("Unsupported jdbc method addBatch(final String sql)");
    }

    @Override
    public void clearBatch() throws SQLException {
        throw new UnsupportedJdbcMethodException("Unsupported jdbc method clearBatch()");
    }

    @Override
    public int[] executeBatch() throws SQLException {
        throw new UnsupportedJdbcMethodException("Unsupported jdbc method executeBatch()");
    }

    @Override
    public final void closeOnCompletion() throws SQLException {
        throw new UnsupportedJdbcMethodException("Unsupported jdbc method closeOnCompletion()");
    }

    @Override
    public final boolean isCloseOnCompletion() throws SQLException {
        throw new UnsupportedJdbcMethodException("Unsupported jdbc method isCloseOnCompletion()");
    }

    @Override
    public final void setCursorName(final String name) throws SQLException {
        throw new UnsupportedJdbcMethodException("Unsupported jdbc method setCursorName(final String name)");
    }

    @Override
    public void setPoolable(boolean b) throws SQLException {
        throw new UnsupportedJdbcMethodException("Unsupported jdbc method setPoolable(boolean b)");
    }
}
