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
import com.github.devx.routing.datasource.RoutingKey;
import com.github.devx.routing.loadbalance.RandomLoadBalancer;
import com.github.devx.routing.sql.parser.SqlStatement;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

/**
 * Route to the specified data source forcefully,
 * and randomly select between multiple data sources
 * if more than one are specified.
 *
 * @author he peng
 * @since 1.0
 *
 * @see RoutingContext#force(String...) ()
 */
public class ForceTargetRoutingRule implements StatementRoutingRule {

    @Override
    public String routing(SqlStatement statement) {

        Set<String> dataSources = RoutingContext.getForceDataSources();
        if (Objects.isNull(dataSources) || dataSources.isEmpty()) {
            return null;
        }

        RandomLoadBalancer balancer = new RandomLoadBalancer(new ArrayList<>(dataSources));
        return balancer.choose();
    }

    @Override
    public int priority() {
        return Integer.MIN_VALUE;
    }

    @Override
    public String routing(RoutingKey key) {
        return null;
    }


}
