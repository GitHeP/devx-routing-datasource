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
