package com.github.devx.routing.rule;

import com.github.devx.routing.config.TableRuleConfiguration;
import com.github.devx.routing.rule.FromTableRoutingRule;
import com.github.devx.routing.sql.parser.JSqlParser;
import com.github.devx.routing.sql.parser.SqlParser;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.setAllowComparingPrivateFields;

/**
 * @author he peng
 * @since 1.0
 */
class FromTableRoutingRuleTest {

    @Test
    void routing() {

        SqlParser sqlParser = new JSqlParser();

        TableRuleConfiguration tableRule = new TableRuleConfiguration();
        Map<String, Set<String>> tables = new HashMap<>();
        Set<String> employeeDatasourceNames = new HashSet<>();
        employeeDatasourceNames.add("read0");
        employeeDatasourceNames.add("read1");
        employeeDatasourceNames.add("read2");
        tables.put("employee" , employeeDatasourceNames);
        tableRule.setTables(tables);
        FromTableRoutingRule routingRule = new FromTableRoutingRule(tableRule);
        String sql1 = "select * from employee where name = 'DevX'";
        String result1 = routingRule.routing(sqlParser.parse(sql1));

        setAllowComparingPrivateFields(true);
        assertThat(employeeDatasourceNames).contains(result1);

        String sql2 = "SELECT e.* , u.* \n" +
                "FROM employee e , users u\n" +
                "WHERE e.user_id = u.id;";
        String result2 = routingRule.routing(sqlParser.parse(sql2));

        assertThat(employeeDatasourceNames).contains(result2);
    }
}