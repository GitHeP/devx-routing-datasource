package com.lp.sql.routing.rule;

import com.lp.sql.routing.RoutingTargetAttribute;
import com.lp.sql.routing.RoutingTargetType;
import com.lp.sql.routing.datasource.DataSourceAttribute;
import com.lp.sql.routing.datasource.RoutingContext;
import com.lp.sql.routing.loadbalance.WeightRandomLoadBalance;
import com.lp.sql.routing.sql.parser.JSqlParser;
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

        List<RoutingTargetAttribute> writes = new ArrayList<>();
        writes.add(new DataSourceAttribute(RoutingTargetType.WRITE , writeDataSourceName , 33));

        List<RoutingTargetAttribute> reads = new ArrayList<>();
        reads.add(new DataSourceAttribute(RoutingTargetType.READ , "read_0" , 33));
        reads.add(new DataSourceAttribute(RoutingTargetType.READ , "read_2" , 33));


        WeightRandomLoadBalance readLoadBalance = new WeightRandomLoadBalance(reads);
        WeightRandomLoadBalance writeLoadBalance = new WeightRandomLoadBalance(writes);

        ForceReadRoutingRule routingRule = new ForceReadRoutingRule(new JSqlParser() , readLoadBalance , writeLoadBalance);

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

        List<RoutingTargetAttribute> writes = new ArrayList<>();
        writes.add(new DataSourceAttribute(RoutingTargetType.WRITE , writeDataSourceName , 33));

        List<RoutingTargetAttribute> reads = new ArrayList<>();
        reads.add(new DataSourceAttribute(RoutingTargetType.READ , "read_0" , 33));
        reads.add(new DataSourceAttribute(RoutingTargetType.READ , "read_2" , 33));


        WeightRandomLoadBalance readLoadBalance = new WeightRandomLoadBalance(reads);
        WeightRandomLoadBalance writeLoadBalance = new WeightRandomLoadBalance(writes);

        ForceReadRoutingRule routingRule = new ForceReadRoutingRule(new JSqlParser() , readLoadBalance , writeLoadBalance);

        String sql = "INSERT INTO area (id, name) VALUES (6, 'Birmingham')";
        String result = routingRule.routing(new RoutingKey().setSql(sql));

        assertThat(result).isNull();
    }
}