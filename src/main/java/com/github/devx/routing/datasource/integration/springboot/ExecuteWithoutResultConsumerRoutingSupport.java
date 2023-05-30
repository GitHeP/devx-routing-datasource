/*
 *    Copyright 2023 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
