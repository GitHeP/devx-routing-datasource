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

import com.github.devx.routing.datasource.config.TableRuleConfiguration;
import com.github.devx.routing.datasource.routing.RoutingKey;
import com.github.devx.routing.datasource.routing.loadbalance.RandomLoadBalancer;
import com.github.devx.routing.datasource.sql.parser.SqlStatement;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Route based on the table name following the FROM fragment
 * to select the corresponding data source configured for that table.
 *
 * @author he peng
 * @since 1.0
 *
 * @see TableRuleConfiguration
 */
public class FromTableRoutingRule implements StatementRoutingRule {

    private final TableRuleConfiguration tableRule;

    public FromTableRoutingRule(TableRuleConfiguration tableRule) {
        this.tableRule = tableRule;
    }

    @Override
    public String routing(SqlStatement statement) {

        if (Objects.isNull(statement.getFromTable())) {
            return null;
        }

        String fromTable = statement.getFromTable();
        List<String> datasourceNames = new ArrayList<>();
        if (tableRule.getTables().containsKey(fromTable)) {
            datasourceNames.addAll(tableRule.getTables().get(fromTable));
        }

        RandomLoadBalancer balancer = new RandomLoadBalancer(datasourceNames);
        return balancer.choose();
    }

    @Override
    public int priority() {
        return 10;
    }

    @Override
    public String routing(RoutingKey key) {
        return null;
    }
}
