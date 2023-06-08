package com.github.devx.routing.rule;

import com.github.devx.routing.RoutingTargetType;
import com.github.devx.routing.config.DataSourceConfiguration;
import com.github.devx.routing.config.DatabaseRoutingConfiguration;
import com.github.devx.routing.config.RoutingConfiguration;
import com.github.devx.routing.config.RoutingRuleConfiguration;
import com.github.devx.routing.config.SqlTypeConfiguration;
import com.github.devx.routing.sql.parser.JSqlParser;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Peng He
 * @since 1.0
 */
class DatabaseRoutingRuleTest {

    @Test
    void routing() {
        RoutingConfiguration routingConfiguration = new RoutingConfiguration();

        List<DataSourceConfiguration> dataSources = new ArrayList<>();
        DataSourceConfiguration dataSourceConfiguration = new DataSourceConfiguration();
        dataSourceConfiguration.setName("write_0");
        dataSourceConfiguration.setType(RoutingTargetType.READ_WRITE);
        dataSourceConfiguration.setWeight(566);
        dataSources.add(dataSourceConfiguration);
        routingConfiguration.setDataSources(dataSources);

        RoutingRuleConfiguration routingRuleConfiguration = new RoutingRuleConfiguration();
        List<DatabaseRoutingConfiguration> databases = new ArrayList<>();
        DatabaseRoutingConfiguration databaseRoutingConfiguration = new DatabaseRoutingConfiguration();
        databaseRoutingConfiguration.setName("test1");
        Map<String, SqlTypeConfiguration> targets = new HashMap<>();
        SqlTypeConfiguration sqlTypeConfiguration = new SqlTypeConfiguration();
        sqlTypeConfiguration.setAllowAllSqlTypes(true);
        targets.put("write_0" , sqlTypeConfiguration);
        databaseRoutingConfiguration.setTargets(targets);
        databases.add(databaseRoutingConfiguration);
        routingRuleConfiguration.setDatabases(databases);
        routingConfiguration.setRules(routingRuleConfiguration);
        DatabaseRoutingRule routingRule = new DatabaseRoutingRule(new JSqlParser(), routingConfiguration);
        String targetName = routingRule.routing(new RoutingKey().setSql("select * from test1.employee where id = ?"));
        assertThat(targetName).isEqualTo("write_0");
    }
}