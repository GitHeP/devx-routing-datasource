package com.github.devx.routing.datasource.routing.rule;

import com.github.devx.routing.datasource.routing.RoutingContext;
import com.github.devx.routing.datasource.sql.parser.JSqlParser;
import com.github.devx.routing.datasource.sql.parser.SqlParser;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author he peng
 * @since 1.0
 */
class ForceTargetRoutingRuleTest {

    @Test
    void routingPositive() {

        String[] dataSourceNames = {"write_0" , "write_1" , "write_2"};
        RoutingContext.force(dataSourceNames);
        assertThat(dataSourceNames).containsAll(RoutingContext.getForceDataSources());

        SqlParser sqlParser = new JSqlParser();
        ForceTargetRoutingRule routingRule = new ForceTargetRoutingRule();
        String sql = "INSERT INTO area (id, name) VALUES (6, 'Birmingham')";
        String result = routingRule.routing(sqlParser.parse(sql));
        assertThat(dataSourceNames).contains(result);
    }

    @Test
    void routingNegative () {

        String[] dataSourceNames = {"write_0" , "write_1" , "write_2"};
        RoutingContext.force(dataSourceNames);
        assertThat(dataSourceNames).containsAll(RoutingContext.getForceDataSources());

        RoutingContext.forceRead();

        SqlParser sqlParser = new JSqlParser();
        ForceTargetRoutingRule routingRule = new ForceTargetRoutingRule();
        String sql = "INSERT INTO area (id, name) VALUES (6, 'Birmingham')";
        String result = routingRule.routing(sqlParser.parse(sql));
        assertThat(result).isNull();
    }

}