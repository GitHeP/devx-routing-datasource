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

package com.github.devx.routing.integration.springboot;

import com.github.devx.routing.datasource.RoutingContext;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

/**
 * @author Peng He
 * @since 1.0
 */

@Deprecated
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
