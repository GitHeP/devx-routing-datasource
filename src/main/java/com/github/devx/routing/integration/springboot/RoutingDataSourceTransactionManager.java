package com.github.devx.routing.integration.springboot;

import com.github.devx.routing.datasource.RoutingContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;

import javax.sql.DataSource;

/**
 * @author Peng He
 * @since 1.0
 */
public class RoutingDataSourceTransactionManager extends DataSourceTransactionManager {

    public RoutingDataSourceTransactionManager(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {
        RoutingContext.setInTx();
        RoutingContext.setTxReadOnly(definition.isReadOnly());
        super.doBegin(transaction, definition);
    }

    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        try {
            super.doCleanupAfterCompletion(transaction);
        } finally {
            RoutingContext.clear();
        }
    }
}
