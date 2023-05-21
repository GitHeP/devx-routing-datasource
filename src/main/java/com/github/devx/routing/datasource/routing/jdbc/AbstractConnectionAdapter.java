package com.github.devx.routing.datasource.routing.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;

/**
 * @author he peng
 * @since 1.0
 */
public abstract class AbstractConnectionAdapter extends AbstractUnsupportedConnection {

    @Override
    public int getHoldability() throws SQLException {
        return ResultSet.CLOSE_CURSORS_AT_COMMIT;
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {

    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {

    }

    @Override
    public String getCatalog() throws SQLException {
        return null;
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {

    }
}
