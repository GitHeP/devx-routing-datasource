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

import com.github.devx.routing.RoutingTargetAttribute;
import com.github.devx.routing.config.RoutingConfiguration;
import com.github.devx.routing.config.SqlTypeConfiguration;
import com.github.devx.routing.loadbalance.WeightRandomLoadBalance;
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
 */
public class TableRoutingRule implements SqlAttributeRoutingRule {

    private final RoutingConfiguration routingConfiguration;

    private final Map<String, Map<String, SqlTypeConfiguration>> tableRule;

    private final Set<String> ruleTables;

    public TableRoutingRule(RoutingConfiguration routingConfiguration) {
        this.routingConfiguration = routingConfiguration;
        this.tableRule = routingConfiguration.getRules().getTables();
        this.ruleTables = tableRule.keySet();
    }



    @Override
    public String routing(SqlAttribute attribute) {

        if (Objects.isNull(attribute.getTables()) || attribute.getTables().isEmpty() || !CollectionUtils.containsAny(ruleTables , attribute.getTables())) {
            return null;
        }

        List<String> targetNames = new ArrayList<>();
        Set<String> tables = attribute.getTables();
        for (String table : tables) {

            Map<String, SqlTypeConfiguration> sqlTypeConfigurationMap = tableRule.get(table);
            if (Objects.isNull(sqlTypeConfigurationMap) || sqlTypeConfigurationMap.isEmpty()) {
                continue;
            }

            for (Map.Entry<String, SqlTypeConfiguration> entry : sqlTypeConfigurationMap.entrySet()) {
                String targetName = entry.getKey();
                SqlTypeConfiguration sqlTypeConfiguration = entry.getValue();
                Set<SqlType> sqlTypes = sqlTypeConfiguration.getSqlTypes();
                boolean matches = Boolean.TRUE.equals(sqlTypeConfiguration.getAllowAllSqlTypes()) || (Objects.nonNull(sqlTypes) && sqlTypes.contains(attribute.getSqlType()));
                if (matches) {
                    targetNames.add(targetName);
                }
            }
        }

        List<RoutingTargetAttribute> routingTargetAttributes = new ArrayList<>();
        for (String targetName : targetNames) {
            RoutingTargetAttribute routingTargetAttribute = routingConfiguration.getRoutingTargetAttribute(targetName);
            if (routingTargetAttribute != null) {
                routingTargetAttributes.add(routingTargetAttribute);
            }
        }

        if (!routingTargetAttributes.isEmpty()) {
            String targetName;
            if (routingTargetAttributes.size() == 1) {
                targetName = routingTargetAttributes.get(0).getName();
            } else {
                WeightRandomLoadBalance loadBalance = new WeightRandomLoadBalance(routingTargetAttributes);
                targetName = loadBalance.choose().getName();
            }
            return targetName;
        }

        return null;
    }

    @Override
    public int priority() {
        return Integer.MAX_VALUE - 500;
    }

    @Override
    public String routing(RoutingKey key) {
        return null;
    }
}
