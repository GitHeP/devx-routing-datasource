package com.github.devx.routing.rule;

import com.github.devx.routing.RoutingTargetAttribute;
import com.github.devx.routing.config.DatabaseRoutingConfiguration;
import com.github.devx.routing.config.RoutingConfiguration;
import com.github.devx.routing.config.SqlTypeConfiguration;
import com.github.devx.routing.loadbalance.WeightRandomLoadBalance;
import com.github.devx.routing.sql.SqlAttribute;
import com.github.devx.routing.sql.SqlType;
import com.github.devx.routing.sql.parser.SqlParser;
import com.github.devx.routing.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Peng He
 * @since 1.0
 */

public class DatabaseRoutingRule implements SqlAttributeRoutingRule {

    private final SqlParser sqlParser;

    private final RoutingConfiguration routingConfiguration;

    private final Map<String , DatabaseRoutingConfiguration> databaseRoutingMap;

    private final Set<String> databaseNames;

    public DatabaseRoutingRule(SqlParser sqlParser, RoutingConfiguration routingConfiguration) {
        this.sqlParser = sqlParser;
        this.routingConfiguration = routingConfiguration;
        this.databaseRoutingMap = new HashMap<>();
        for (DatabaseRoutingConfiguration conf : routingConfiguration.getRules().getDatabases()) {
            this.databaseRoutingMap.put(conf.getName() , conf);
        }
        this.databaseNames = this.databaseRoutingMap.keySet();
    }

    @Override
    public int priority() {
        return Integer.MAX_VALUE - 500;
    }

    @Override
    public String routing(RoutingKey key) {
        return routing(sqlParser.parse(key.getSql()));
    }

    @Override
    public String routing(SqlAttribute sqlAttribute) {

        Set<String> sqlDatabases = sqlAttribute.getDatabases();
        boolean skipRule = sqlDatabases == null || sqlDatabases.isEmpty() || databaseNames.isEmpty() || !CollectionUtils.containsAny(databaseNames, sqlAttribute.getDatabases());
        if (skipRule) {
            return null;
        }

        List<RoutingTargetAttribute> routingTargetAttributes = matchRoutingTarget(sqlAttribute);
        if (!routingTargetAttributes.isEmpty()) {
            String nodeName;
            if (routingTargetAttributes.size() == 1) {
                nodeName = routingTargetAttributes.get(0).getName();
            } else {
                WeightRandomLoadBalance loadBalance = new WeightRandomLoadBalance(routingTargetAttributes);
                nodeName = loadBalance.choose().getName();
            }
            return nodeName;
        }

        return null;
    }

    private List<RoutingTargetAttribute> matchRoutingTarget(SqlAttribute sqlAttribute) {
        List<String> targetNames = matchRoutingTargetName(sqlAttribute);
        List<RoutingTargetAttribute> routingTargetAttributes = new ArrayList<>();
        for (String targetName : targetNames) {
            RoutingTargetAttribute routingTargetAttribute = routingConfiguration.getRoutingTargetAttribute(targetName);
            if (routingTargetAttribute != null) {
                routingTargetAttributes.add(routingTargetAttribute);
            }
        }
        return routingTargetAttributes;
    }
    private List<String> matchRoutingTargetName(SqlAttribute sqlAttribute) {
        List<String> targetNames = new ArrayList<>();

        for (String database : sqlAttribute.getDatabases()) {
            if (databaseRoutingMap.containsKey(database)) {
                DatabaseRoutingConfiguration conf = databaseRoutingMap.get(database);
                Map<String, SqlTypeConfiguration> targets = conf.getNodes();
                if (targets != null && !targets.isEmpty()) {
                    for (Map.Entry<String, SqlTypeConfiguration> entry : targets.entrySet()) {
                        String targetName = entry.getKey();
                        SqlTypeConfiguration sqlTypeConfiguration = entry.getValue();
                        Set<SqlType> sqlTypes = sqlTypeConfiguration.getSqlTypes();
                        boolean matches = Boolean.TRUE.equals(sqlTypeConfiguration.getAllowAllSqlTypes()) || (Objects.nonNull(sqlTypes) && sqlTypes.contains(sqlAttribute.getSqlType()));
                        if (matches) {
                            targetNames.add(targetName);
                        }
                    }
                }
            }
        }
        return targetNames;
    }
}
