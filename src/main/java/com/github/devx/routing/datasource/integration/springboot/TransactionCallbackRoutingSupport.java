package com.github.devx.routing.datasource.integration.springboot;

import com.github.devx.routing.datasource.routing.RoutingContext;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

/**
 * @author he peng
 * @since 1.0
 */
public class TransactionCallbackRoutingSupport<T> implements TransactionCallback<T> {

    private final TransactionCallback<T> callback;

    public TransactionCallbackRoutingSupport(TransactionCallback<T> callback) {
        this.callback = callback;
    }

    @Override
    public T doInTransaction(TransactionStatus status) {

        RoutingContext.setInTx();
        RoutingContext.setTxReadOnly(status.isRollbackOnly());
        try {
            return callback.doInTransaction(status);
        } finally {
            RoutingContext.clear();
        }
    }
}
