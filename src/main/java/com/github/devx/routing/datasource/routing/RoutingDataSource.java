package com.github.devx.routing.datasource.routing;

import javax.sql.DataSource;

/**
 * The data source that provides the ability to route multiple data sources,
 * each with a unique name, supports routing based on data source name
 * and SQL statements.
 *
 * @author he peng
 * @since 1.0
 */
public interface RoutingDataSource extends DataSource {

    /**
     * Select the data source instance with the specified data
     * source name based on data source name routing
     *
     * @param name data source name
     * @return DataSource
     */
    DataSource getDataSourceWithName(String name);

    /**
     * Route based on SQL, queries are routed to read data sources
     * and write statements are routed to write data sources.
     * In transaction scenarios, all SQL executions are routed
     * to write data sources; while in read-only transaction scenarios,
     * all SQL executions are routed to read data sources.
     * Other JDBC API calls unrelated to SQL execution will be routed
     * to write data sources for execution.
     *
     * @param sql sql
     * @return DataSource
     */
    DataSource getDataSourceWithSql(String sql);

    /**
     * Get the write data source instance.
     *
     * @return DataSource
     */
    DataSource getWriteDataSource();

}
