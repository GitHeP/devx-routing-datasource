package com.github.devx.routing.datasource.routing.rule;

import com.github.devx.routing.datasource.routing.loadbalance.LoadBalancer;
import com.github.devx.routing.datasource.sql.parser.SqlParser;
import com.github.devx.routing.datasource.sql.parser.SqlStatement;

import java.util.Set;

/**
 * Write statements will be routed to the write data source,
 * and read statements will be routed to the read data source,
 * except when in a transaction.
 *
 * @author he peng
 * @since 1.0
 */
public class ReadWriteSplittingRoutingRule extends AbstractRoutingRule {


    public ReadWriteSplittingRoutingRule(SqlParser sqlParser, LoadBalancer<String> loadBalancer, String writeDataSourceName, Set<String> readDataSourceNames) {
        super(sqlParser, loadBalancer, writeDataSourceName, readDataSourceNames);
    }

    @Override
    public int priority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public String routing(SqlStatement statement) {
        return statement.isWrite() ? writeDataSourceName : loadBalancer.choose();
    }
}
