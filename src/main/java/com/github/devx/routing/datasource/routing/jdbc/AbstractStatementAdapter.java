package com.github.devx.routing.datasource.routing.jdbc;

import java.sql.SQLException;
import java.sql.SQLWarning;

/**
 * @author he peng
 * @since 1.0
 */
public abstract class AbstractStatementAdapter extends AbstractUnsupportedStatement {


    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {

    }


    @Override
    public boolean isPoolable() throws SQLException {
        return false;
    }
}
