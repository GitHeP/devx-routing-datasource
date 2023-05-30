package com.github.devx.routing.datasource.routing;

import com.github.devx.routing.datasource.exception.NoSuchDataSourceException;
import com.github.devx.routing.datasource.routing.jdbc.RoutingConnection;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * @author he peng
 * @since 1.0
 */
public abstract class AbstractRoutingDataSource implements RoutingDataSource {

    protected final Map<String , DataSource> dataSources = new ConcurrentHashMap<>();

    protected AbstractRoutingDataSource(Map<String , DataSource> dataSources) {
        if (Objects.isNull(dataSources) || dataSources.isEmpty()) {
            throw new NoSuchDataSourceException("Data sources cannot be empty.");
        }
        this.dataSources.putAll(dataSources);
    }

    @Override
    public DataSource getDataSourceWithName(String name) {
        DataSource dataSource = dataSources.get(name);
        if (Objects.isNull(dataSource)) {
            throw new NoSuchDataSourceException(String.format("No DataSource with name [%s] found" , name));
        }
        RoutingContext.setRoutedDataSourceName(name);
        return dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return new RoutingConnection(this);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return Objects.requireNonNull(getDataSource()).getConnection(username , password);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return Objects.requireNonNull(getDataSource()).unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return Objects.requireNonNull(getDataSource()).isWrapperFor(iface);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return Objects.requireNonNull(getDataSource()).getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        Objects.requireNonNull(getDataSource()).setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        Objects.requireNonNull(getDataSource()).setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return Objects.requireNonNull(getDataSource()).getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return Objects.requireNonNull(getDataSource()).getParentLogger();
    }

    /**
     * get an internal DataSource instance
     * @return DataSource
     */
    protected abstract DataSource getDataSource();
}
