package com.github.devx.routing.datasource.routing.rule;

import com.github.devx.routing.datasource.config.TableRuleConfiguration;
import com.github.devx.routing.datasource.sql.parser.JSqlParser;
import com.github.devx.routing.datasource.sql.parser.SqlParser;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.setAllowComparingPrivateFields;

/**
 * @author: he peng
 * @create: 2023/5/29 15:50
 * @description:
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
        String sql = "select * from employee where name = 'DevX'";
        String result = routingRule.routing(sqlParser.parse(sql));

        setAllowComparingPrivateFields(true);
        assertThat(employeeDatasourceNames).contains(result);
    }
}