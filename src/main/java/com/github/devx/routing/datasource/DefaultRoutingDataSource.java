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

package com.github.devx.routing.datasource;

import com.github.devx.routing.rule.RoutingRule;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @author he peng
 * @since 1.0
 */
public class DefaultRoutingDataSource extends AbstractRoutingDataSource {

    private final RoutingRule rule;

    private final RoutingKeyProvider routingKeyProvider;

    public DefaultRoutingDataSource(Map<String, DataSource> dataSources, RoutingRule rule, RoutingKeyProvider routingKeyProvider) {
        super(dataSources);
        this.rule = rule;
        this.routingKeyProvider = routingKeyProvider;
    }

    @Override
    public DataSource getDataSourceWithSql(String sql) {
        return getDataSourceWithName(rule.routing(new RoutingKey().setSql(sql)));
    }

    @Override
    public DataSource getWriteDataSource() {
        return getDataSourceWithName(rule.routing(new RoutingKey().setForeWriteDataSource(true)));
    }

    @Override
    protected DataSource getDataSource() {
        return getDataSourceWithName(rule.routing(routingKeyProvider.getRoutingKey()));
    }
}
