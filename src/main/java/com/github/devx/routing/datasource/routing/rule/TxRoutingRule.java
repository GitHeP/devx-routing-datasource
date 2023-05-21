package com.github.devx.routing.datasource.routing.rule;

import com.github.devx.routing.datasource.routing.RoutingContext;
import com.github.devx.routing.datasource.routing.loadbalance.LoadBalancer;
import com.github.devx.routing.datasource.sql.parser.SqlParser;
import com.github.devx.routing.datasource.sql.parser.SqlStatement;

import java.util.Objects;
import java.util.Set;

/**
 * @author he peng
 * @since 1.0
 */
public class TxRoutingRule extends AbstractRoutingRule {


    public TxRoutingRule(SqlParser sqlParser, LoadBalancer<String> loadBalancer, String writeDataSourceName, Set<String> readDataSourceNames) {
        super(sqlParser, loadBalancer, writeDataSourceName, readDataSourceNames);
    }

    @Override
    public int priority() {
        return Integer.MIN_VALUE + 10;
    }

    @Override
    protected String internalRouting(SqlStatement statement) {

        boolean inTx = RoutingContext.inTx();
        if (!inTx) {
            return null;
        }

        // read only tx
        boolean txReadOnly = RoutingContext.getTxReadOnly() && Objects.nonNull(statement) && statement.isRead();
        return txReadOnly ? loadBalancer.choose() : writeDataSourceName;
    }
}
