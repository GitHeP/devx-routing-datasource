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

import com.github.devx.routing.datasource.RoutingKey;
import com.github.devx.routing.loadbalance.LoadBalancer;
import com.github.devx.routing.sql.SqlAttribute;
import com.github.devx.routing.sql.parser.SqlParser;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author Peng He
 * @since 1.0
 */
public abstract class AbstractRoutingRule implements SqlAttributeRoutingRule {

    protected final SqlParser sqlParser;

    protected final LoadBalancer<String> loadBalancer;

    protected final String writeDataSourceName;

    protected final Set<String> readDataSourceNames;

    protected AbstractRoutingRule(SqlParser sqlParser , LoadBalancer<String> loadBalancer , String writeDataSourceName , Set<String> readDataSourceNames) {
        this.sqlParser = sqlParser;
        this.loadBalancer = loadBalancer;
        this.writeDataSourceName = writeDataSourceName;
        if (Objects.isNull(readDataSourceNames)) {
            readDataSourceNames = new HashSet<>();
        }
        this.readDataSourceNames = Collections.synchronizedSet(readDataSourceNames);
    }

    @Override
    public String routing(RoutingKey key) {
        SqlAttribute sqlAttribute = null;
        if (Objects.nonNull(key) && Objects.nonNull(key.getSql())) {
            sqlAttribute = sqlParser.parse(key.getSql());
        }
        return routing(sqlAttribute);
    }

}
