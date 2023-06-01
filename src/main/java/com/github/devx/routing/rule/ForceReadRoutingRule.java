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

import com.github.devx.routing.datasource.RoutingContext;
import com.github.devx.routing.loadbalance.LoadBalancer;
import com.github.devx.routing.sql.parser.SqlParser;
import com.github.devx.routing.sql.parser.SqlStatement;

import java.util.Set;

/**
 * Force routing to the read data source.
 *
 * @author Peng He
 * @since 1.0
 *
 * @see RoutingContext#forceRead()
 */
public class ForceReadRoutingRule extends AbstractRoutingRule {

    public ForceReadRoutingRule(SqlParser sqlParser, LoadBalancer<String> loadBalancer, String writeDataSourceName, Set<String> readDataSourceNames) {
        super(sqlParser, loadBalancer, writeDataSourceName, readDataSourceNames);
    }

    @Override
    public String routing(SqlStatement statement) {
        return RoutingContext.isForceReadDataSource() ? loadBalancer.choose() : null;
    }

    @Override
    public int priority() {
        return Integer.MIN_VALUE;
    }
}
