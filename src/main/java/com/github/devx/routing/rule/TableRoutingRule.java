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

import com.github.devx.routing.config.SqlTypeConfiguration;
import com.github.devx.routing.config.TableRuleConfiguration;
import com.github.devx.routing.datasource.RoutingKey;
import com.github.devx.routing.loadbalance.RandomLoadBalancer;
import com.github.devx.routing.sql.SqlAttribute;
import com.github.devx.routing.sql.SqlType;
import com.github.devx.routing.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Route based on the table name following the FROM fragment
 * to select the corresponding data source configured for that table.
 *
 * @author Peng He
 * @since 1.0
 *
 * @see TableRuleConfiguration
 */
public class TableRoutingRule implements SqlAttributeRoutingRule {

    private final Map<String, Map<String, SqlTypeConfiguration>> tableRule;

    private final Set<String> ruleTables;

    public TableRoutingRule(Map<String, Map<String, SqlTypeConfiguration>> tableRule) {
        this.tableRule = tableRule;
        this.ruleTables = tableRule.keySet();
    }



    @Override
    public String routing(SqlAttribute attribute) {

        if (Objects.isNull(attribute.getTables()) || attribute.getTables().isEmpty() || !CollectionUtils.containsAny(ruleTables , attribute.getTables())) {
            return null;
        }

        List<String> datasourceNames = new ArrayList<>();
        Set<String> tables = attribute.getTables();
        for (String table : tables) {

            Map<String, SqlTypeConfiguration> sqlTypeConfigurationMap = tableRule.get(table);
            if (Objects.isNull(sqlTypeConfigurationMap) || sqlTypeConfigurationMap.isEmpty()) {
                continue;
            }

            for (Map.Entry<String, SqlTypeConfiguration> entry : sqlTypeConfigurationMap.entrySet()) {
                String datasourceName = entry.getKey();
                SqlTypeConfiguration sqlTypeConfiguration = entry.getValue();
                if (Boolean.TRUE.equals(sqlTypeConfiguration.getAllowAllSqlTypes())) {
                    datasourceNames.add(datasourceName);
                }

                Set<SqlType> sqlTypes = sqlTypeConfiguration.getSqlTypes();
                if (Objects.nonNull(sqlTypes) && sqlTypes.contains(attribute.getSqlType())) {
                    datasourceNames.add(datasourceName);
                }
            }
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
