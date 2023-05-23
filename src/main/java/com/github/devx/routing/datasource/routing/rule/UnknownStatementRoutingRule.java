package com.github.devx.routing.datasource.routing.rule;

import com.github.devx.routing.datasource.routing.loadbalance.LoadBalancer;
import com.github.devx.routing.datasource.sql.parser.SqlParser;
import com.github.devx.routing.datasource.sql.parser.SqlStatement;

import java.util.Objects;
import java.util.Set;

/**
 * When the SQL cannot be parsed by the SqlParser,
 * data source routing based on the SQL cannot be performed.
 * It will be routed to the write data source by default.
 *
 * @author he peng
 * @since 1.0
 *
 * @see SqlParser
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
