package com.github.devx.routing.datasource.routing.rule;

import com.github.devx.routing.datasource.routing.loadbalance.LoadBalancer;
import com.github.devx.routing.datasource.sql.parser.SqlParser;
import com.github.devx.routing.datasource.sql.parser.SqlStatement;

import java.util.Objects;
import java.util.Set;

/**
 * @author he peng
 * @since 1.0
 */
public class UnknownStatementRoutingRule extends AbstractRoutingRule {


    public UnknownStatementRoutingRule(SqlParser sqlParser, LoadBalancer<String> loadBalancer, String writeDataSourceName, Set<String> readDataSourceNames) {
        super(sqlParser, loadBalancer, writeDataSourceName, readDataSourceNames);
    }

    @Override
    public String routing(SqlStatement statement) {
        return Objects.isNull(statement) ? writeDataSourceName : null;
    }

    @Override
    public int priority() {
        return Integer.MIN_VALUE + 1;
    }
}
