package com.github.devx.routing.datasource.routing;

import com.github.devx.routing.datasource.routing.rule.RoutingRule;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @author he peng
 * @since 1.0
 */
public class DefaultRoutingDataSource extends AbstractRoutingDataSource {

    private final RoutingRule rule;

    private final RoutingKeyProvider routingKeyProvider;

    public DefaultRoutingDataSource(Map<String, DataSource> dataSources, RoutingRule rule, RoutingKeyProvider routingKeyProvider) {
        super(dataSources);
        this.rule = rule;
        this.routingKeyProvider = routingKeyProvider;
    }

    @Override
    public DataSource getDataSourceWithSql(String sql) {
        return getDataSourceWithName(rule.routing(new RoutingKey().setSql(sql)));
    }

    @Override
    public DataSource getWriteDataSource() {
        return getDataSourceWithName(rule.routing(new RoutingKey().setForeWriteDataSource(true)));
    }

    @Override
    protected DataSource getDataSource() {
        return getDataSourceWithName(rule.routing(routingKeyProvider.getRoutingKey()));
    }
}
