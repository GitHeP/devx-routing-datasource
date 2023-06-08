package com.github.devx.routing.rule;

import com.github.devx.routing.config.DatabaseRoutingConfiguration;
import com.github.devx.routing.sql.SqlAttribute;
import com.github.devx.routing.sql.parser.SqlParser;
import com.github.devx.routing.util.CollectionUtils;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Peng He
 * @since 1.0
 */

public class DatabaseRoutingRule implements SqlAttributeRoutingRule {

    private final SqlParser sqlParser;

    private final Set<DatabaseRoutingConfiguration> databaseRoutingConfigs;

    private final Set<String> databaseNames;

    public DatabaseRoutingRule(SqlParser sqlParser, Set<DatabaseRoutingConfiguration> databaseRoutingConfigs) {
        this.sqlParser = sqlParser;
        this.databaseRoutingConfigs = databaseRoutingConfigs;
        this.databaseNames = this.databaseRoutingConfigs.stream().map(DatabaseRoutingConfiguration::getName).collect(Collectors.toSet());
    }

    @Override
    public int priority() {
        return Integer.MAX_VALUE - 400;
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

        for (String database : sqlAttribute.getDatabases()) {

        }


        return null;
    }
}
