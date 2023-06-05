package com.github.devx.routing.rule;

import com.github.devx.routing.datasource.RoutingContext;
import com.github.devx.routing.loadbalance.RoundRobinLoadBalancer;
import com.github.devx.routing.sql.parser.JSqlParser;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Peng He
 * @since 1.0
 */
class ForceReadRoutingRuleTest {

    @Test
    void routingPositive() {

        RoutingContext.forceRead();
        String writeDataSourceName = "write_0";
        Set<String> readDataSourceNames = new HashSet<>();
        readDataSourceNames.add("read_0");
        readDataSourceNames.add("read_1");
        readDataSourceNames.add("read_2");

        List<String> options = new ArrayList<>(readDataSourceNames);
        ForceReadRoutingRule routingRule = new ForceReadRoutingRule(new JSqlParser() , new RoundRobinLoadBalancer(options) , writeDataSourceName , readDataSourceNames);

        String sql = "INSERT INTO area (id, name) VALUES (6, 'Birmingham')";
        String result = routingRule.routing(new RoutingKey().setSql(sql));

        assertThat(readDataSourceNames).contains(result);
    }

    @Test
    void routingNegative () {

        String writeDataSourceName = "write_0";
        Set<String> readDataSourceNames = new HashSet<>();
        readDataSourceNames.add("read_0");
        readDataSourceNames.add("read_1");
        readDataSourceNames.add("read_2");

        List<String> options = new ArrayList<>(readDataSourceNames);
        ForceReadRoutingRule routingRule = new ForceReadRoutingRule(new JSqlParser() , new RoundRobinLoadBalancer(options) , writeDataSourceName , readDataSourceNames);

        String sql = "INSERT INTO area (id, name) VALUES (6, 'Birmingham')";
        String result = routingRule.routing(new RoutingKey().setSql(sql));

        assertThat(result).isNull();
    }
}