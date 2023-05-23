package com.github.devx.routing.datasource.routing.rule;

import com.github.devx.routing.datasource.routing.RoutingContext;
import com.github.devx.routing.datasource.routing.loadbalance.LoadBalancer;
import com.github.devx.routing.datasource.sql.parser.SqlParser;
import com.github.devx.routing.datasource.sql.parser.SqlStatement;

import java.util.Set;

/**
 * When transactions exist, the routing rules are as follows:
 * If a read-only transaction exists and the current SQL being
 * executed is a read statement, it will be routed to the read
 * data source. If the transaction is not read-only and there
 * are write statements in the transaction, all SQL statements
 * in the transaction will be routed to the write data source
 * for execution.
 *
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
    public String routing(SqlStatement statement) {

        boolean inTx = RoutingContext.inTx();
        if (!inTx) {
            return null;
        }

        // read only tx
        return RoutingContext.getTxReadOnly() ? loadBalancer.choose() : writeDataSourceName;
    }
}
