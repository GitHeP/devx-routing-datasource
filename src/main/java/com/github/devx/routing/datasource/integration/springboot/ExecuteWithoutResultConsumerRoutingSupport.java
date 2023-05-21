package com.github.devx.routing.datasource.integration.springboot;

import com.github.devx.routing.datasource.routing.RoutingContext;
import org.springframework.transaction.TransactionStatus;

import java.util.function.Consumer;

/**
 * @author he peng
 * @since 1.0
 */
public class ExecuteWithoutResultConsumerRoutingSupport implements Consumer<TransactionStatus> {

    private final Consumer<TransactionStatus> consumer;

    public ExecuteWithoutResultConsumerRoutingSupport(Consumer<TransactionStatus> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void accept(TransactionStatus status) {
        RoutingContext.setInTx();
        RoutingContext.setTxReadOnly(status.isRollbackOnly());
        try {
            consumer.accept(status);
        } finally {
            RoutingContext.clear();
        }
    }

}
