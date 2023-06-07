package com.lp.sql.routing.integration.springboot;

import com.lp.sql.routing.RoutingContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;

import javax.sql.DataSource;

/**
 * @author Peng He
 * @since 1.0
 */
public class RoutingDataSourceTransactionManager extends DataSourceTransactionManager {

    public RoutingDataSourceTransactionManager() {
    }

    public RoutingDataSourceTransactionManager(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected void doBegin(Object o, TransactionDefinition definition) throws TransactionException {
        RoutingContext.setInTx();
        RoutingContext.setTxReadOnly(definition.isReadOnly());
        super.doBegin(o , definition);
    }

    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        super.doCleanupAfterCompletion(transaction);
        RoutingContext.clear();
    }
}
