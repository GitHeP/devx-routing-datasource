package com.github.devx.routing.datasource.routing.rule;

import com.github.devx.routing.datasource.routing.loadbalance.LoadBalancer;
import com.github.devx.routing.datasource.sql.parser.SqlParser;
import com.github.devx.routing.datasource.sql.parser.SqlStatement;

import java.util.Set;

/**
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
    protected String internalRouting(SqlStatement statement) {

        String choose = null;
        if (statement.isWrite()) {
            choose = writeDataSourceName;
        } else if (statement.isRead()) {
            choose = loadBalancer.choose();
        }
        return choose;
    }
}
