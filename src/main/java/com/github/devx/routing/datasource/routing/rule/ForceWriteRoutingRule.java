package com.github.devx.routing.datasource.routing.rule;

import com.github.devx.routing.datasource.routing.RoutingContext;
import com.github.devx.routing.datasource.routing.loadbalance.LoadBalancer;
import com.github.devx.routing.datasource.sql.parser.SqlParser;
import com.github.devx.routing.datasource.sql.parser.SqlStatement;

import java.util.Set;

/**
 * @author he peng
 * @since 1.0
 */
public class ForceWriteRoutingRule extends AbstractRoutingRule {

    public ForceWriteRoutingRule(SqlParser sqlParser, LoadBalancer<String> loadBalancer, String writeDataSourceName, Set<String> readDataSourceNames) {
        super(sqlParser, loadBalancer, writeDataSourceName, readDataSourceNames);
    }

    @Override
    public String routing(SqlStatement statement) {
        return RoutingContext.getForceWriteDataSource() ? writeDataSourceName : null;
    }

    @Override
    public int priority() {
        return Integer.MIN_VALUE;
    }
}
