package com.github.devx.routing.datasource.routing.jdbc;

import com.github.devx.routing.datasource.exception.UnsupportedJdbcMethodException;

import java.sql.SQLException;
import java.sql.Wrapper;

/**
 * @author he peng
 * @since 1.0
 */
public class WrapperAdapter implements Wrapper {

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (isWrapperFor(iface)) {
            return (T) this;
        }
        throw new UnsupportedJdbcMethodException(String.format("Unsupported jdbc method unwrap , [%s] can not unwrap as [%s]" , getClass().getName() , iface.getName()));
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this);
    }
}
