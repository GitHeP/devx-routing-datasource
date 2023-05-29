package com.github.devx.routing.datasource.routing.rule;

import com.github.devx.routing.datasource.routing.RoutingContext;
import com.github.devx.routing.datasource.routing.loadbalance.LoadBalancer;
import com.github.devx.routing.datasource.sql.parser.SqlParser;
import com.github.devx.routing.datasource.sql.parser.SqlStatement;

import java.util.Set;

/**
 * Force routing to the read data source.
 *
 * @author he peng
 * @since 1.0
 *
 * @see RoutingContext#forceRead()
 */
public class ForceReadRoutingRule extends AbstractRoutingRule {

    public ForceReadRoutingRule(SqlParser sqlParser, LoadBalancer<String> loadBalancer, String writeDataSourceName, Set<String> readDataSourceNames) {
        super(sqlParser, loadBalancer, writeDataSourceName, readDataSourceNames);
    }

    @Override
    public String routing(SqlStatement statement) {
        return RoutingContext.isForceReadDataSource() ? loadBalancer.choose() : null;
    }

    @Override
    public int priority() {
        return Integer.MIN_VALUE;
    }
}
