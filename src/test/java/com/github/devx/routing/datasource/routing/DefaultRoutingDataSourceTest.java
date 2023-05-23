package com.github.devx.routing.datasource.routing;

import com.github.devx.routing.datasource.routing.loadbalance.LoadBalancer;
import com.github.devx.routing.datasource.routing.loadbalance.RandomLoadBalancer;
import com.github.devx.routing.datasource.routing.rule.CompositeRoutingRule;
import com.github.devx.routing.datasource.routing.rule.ForceWriteRoutingRule;
import com.github.devx.routing.datasource.routing.rule.ReadWriteSplittingRoutingRule;
import com.github.devx.routing.datasource.routing.rule.RoutingRule;
import com.github.devx.routing.datasource.routing.rule.StatementRoutingRule;
import com.github.devx.routing.datasource.routing.rule.TxRoutingRule;
import com.github.devx.routing.datasource.routing.rule.UnknownStatementRoutingRule;
import com.github.devx.routing.datasource.sql.parser.JSqlParser;
import com.github.devx.routing.datasource.sql.parser.SqlParser;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;




/**
 * @author he peng
 * @since 1.0
 */


class DefaultRoutingDataSourceTest {

    static DataSource dataSource;

    @BeforeAll
    static void initDataSource() {


        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:~/test");
        config.setUsername("sa");
        config.setPassword("");
        config.setMinimumIdle(5);
        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(30000);
        config.setAutoCommit(false);

        HikariDataSource writeDataSource = new HikariDataSource(config);
        HikariDataSource readDataSource0 = new HikariDataSource(config);
        HikariDataSource readDataSource1 = new HikariDataSource(config);

        String writeDataSourceName = "write";
        String readDataSource0Name = "read0";
        String readDataSource1Name = "read1";

        Set<String> readDataSourceNames = new HashSet<>();
        readDataSourceNames.add(readDataSource0Name);
        readDataSourceNames.add(readDataSource1Name);

        Map<String, DataSource> dataSources = new HashMap<>();
        dataSources.put(writeDataSourceName , writeDataSource);
        dataSources.put(readDataSource0Name , readDataSource0);
        dataSources.put(readDataSource1Name , readDataSource1);


        SqlParser sqlParser = new JSqlParser();
        LoadBalancer<String> loadBalancer = new RandomLoadBalancer(new ArrayList<>(readDataSourceNames));
        UnknownStatementRoutingRule unknownStatementRoutingRule = new UnknownStatementRoutingRule(sqlParser, loadBalancer, writeDataSourceName, readDataSourceNames);
        TxRoutingRule txRoutingRule = new TxRoutingRule(sqlParser, loadBalancer, writeDataSourceName , readDataSourceNames);
        ReadWriteSplittingRoutingRule readWriteSplittingRoutingRule = new ReadWriteSplittingRoutingRule(sqlParser, loadBalancer, writeDataSourceName, readDataSourceNames);
        ForceWriteRoutingRule forceWriteRoutingRule = new ForceWriteRoutingRule(sqlParser, loadBalancer, writeDataSourceName, readDataSourceNames);

        List<StatementRoutingRule> routingRules = new ArrayList<>();
        routingRules.add(unknownStatementRoutingRule);
        routingRules.add(txRoutingRule);
        routingRules.add(readWriteSplittingRoutingRule);
        routingRules.add(forceWriteRoutingRule);

        RoutingRule rule = new CompositeRoutingRule(sqlParser, loadBalancer, writeDataSourceName, readDataSourceNames , routingRules);
        RoutingKeyProvider routingKeyProvider = new RoutingContextRoutingKeyProvider();
        dataSource = new DefaultRoutingDataSource(dataSources , rule , routingKeyProvider);


        Flyway flyway = Flyway.configure().dataSource(writeDataSource).load();
        flyway.migrate();

    }

    @Test
    void testGetEmployeeNamesAndDepartmentNamesByArea() throws Exception {

        String sql = "SELECT e.name AS employee_name, d.name AS department_name " +
                "FROM employee e " +
                "INNER JOIN department d ON e.department_id = d.id " +
                "WHERE d.area_id = 1";

        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);

        ResultSet rs = stmt.executeQuery();

        List<Map<String, Object>> resultList = new ArrayList<>();
        while (rs.next()) {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("employee_name", rs.getString("employee_name"));
            resultMap.put("department_name", rs.getString("department_name"));
            resultList.add(resultMap);
        }

        assertThat(resultList)
                .extracting("employee_name", "department_name")
                .containsExactlyInAnyOrder(
                        tuple("John Doe", "Research and Development"),
                        tuple("Jane Doe", "Research and Development")
                );

        // close
        close(rs , stmt , conn);
    }

    private void close(ResultSet rs , PreparedStatement stmt , Connection conn) throws Exception {
        rs.close();
        stmt.close();
        conn.close();
    }
}