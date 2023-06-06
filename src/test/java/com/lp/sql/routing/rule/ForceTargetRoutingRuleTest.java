package com.lp.sql.routing.rule;

import com.lp.sql.routing.datasource.RoutingContext;
import com.lp.sql.routing.sql.parser.JSqlParser;
import com.lp.sql.routing.sql.parser.SqlParser;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Peng He
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