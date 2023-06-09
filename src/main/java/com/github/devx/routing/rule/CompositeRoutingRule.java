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

package com.github.devx.routing.rule;

import com.github.devx.routing.loadbalance.LoadBalancer;
import com.github.devx.routing.sql.SqlAttribute;
import com.github.devx.routing.sql.parser.SqlParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * The composite RoutingRule implementation includes multiple
 * RoutingRule instances internally, which will be executed
 * in order according to priority. During the process,
 * if any RoutingRule returns a non-null result, the execution
 * process will be terminated, and the subsequent RoutingRules
 * will not be executed.
 *
 * @author Peng He
 * @since 1.0
 *
 * @see UnknownSqlAttributeRoutingRule
 * @see ForceWriteRoutingRule
 * @see TxRoutingRule
 * @see ReadWriteSplittingRoutingRule
 * @see TableRoutingRule
 */
public class CompositeRoutingRule extends AbstractRoutingRule {

    private final List<SqlAttributeRoutingRule> routingRules;

    public CompositeRoutingRule(SqlParser sqlParser, LoadBalancer<String> loadBalancer, String writeDataSourceName, Set<String> readDataSourceNames, List<SqlAttributeRoutingRule> routingRules) {
        super(sqlParser, loadBalancer, writeDataSourceName, readDataSourceNames);
        List<SqlAttributeRoutingRule> rules = new ArrayList<>();
        if (Objects.nonNull(routingRules)) {
            rules.addAll(routingRules);
            rules.sort(Comparator.comparingInt(PriorityRoutingRule::priority));
        }
        this.routingRules = Collections.unmodifiableList(rules);
    }

    @Override
    public String routing(SqlAttribute attribute) {

        String datasourceName = null;
        for (SqlAttributeRoutingRule routingRule : routingRules) {
            datasourceName = routingRule.routing(attribute);
            if (Objects.nonNull(datasourceName) && datasourceName.length() != 0) {
                break;
            }
        }
        return datasourceName;
    }

    @Override
    public int priority() {
        return Integer.MIN_VALUE;
    }
}
