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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
        config.setJdbcUrl("jdbc:h2:~/test1");
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
        dataSources.put(writeDataSourceName , new DataSourceWrapper(writeDataSource , DataSourceMode.READ_WRITE , writeDataSourceName));
        dataSources.put(readDataSource0Name , new DataSourceWrapper(readDataSource0 , DataSourceMode.READ , readDataSource0Name));
        dataSources.put(readDataSource1Name , new DataSourceWrapper(readDataSource1 , DataSourceMode.READ , readDataSource1Name));


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


        Flyway flyway1 = Flyway.configure().dataSource(writeDataSource).load();
        flyway1.migrate();

        Flyway flyway2 = Flyway.configure().dataSource(readDataSource0).load();
        flyway2.migrate();

        Flyway flyway3 = Flyway.configure().dataSource(readDataSource1).load();
        flyway3.migrate();

    }

    @AfterAll
    static void clearDataBase() throws Exception {
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        statement.execute("DROP ALL OBJECTS");
        statement.close();
        connection.close();
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

    @Test
    void testTxInsert() throws Exception {

        String sql1 = "INSERT INTO area (id, name) VALUES (5, 'New York')";

        Connection conn = dataSource.getConnection();
        conn.setAutoCommit(false);
        conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

        PreparedStatement stmt1 = conn.prepareStatement(sql1);

        int row1 = stmt1.executeUpdate();
        stmt1.close();
        assertThat(row1).isEqualTo(1);


        String sql2 = "INSERT INTO department (id, name, area_id) VALUES  (5, 'Research', 5)";
        PreparedStatement stmt2 = conn.prepareStatement(sql2);

        int row2 = stmt2.executeUpdate();
        stmt2.close();
        assertThat(row2).isEqualTo(1);

        String sql3 = "INSERT INTO employee (id, name, department_id) VALUES (6, 'Doge Lee', 5) ";
        PreparedStatement stmt3 = conn.prepareStatement(sql3);

        int row3 = stmt3.executeUpdate();
        stmt3.close();
        assertThat(row3).isEqualTo(1);

        String sql4 = "SELECT e.name AS employee_name, d.name AS department_name , a.name AS area_name " +
                "FROM employee e " +
                "INNER JOIN department d ON e.department_id = d.id " +
                "INNER JOIN area a ON d.area_id = a.id " +
                "WHERE e.id = ?";

        PreparedStatement stmt4 = conn.prepareStatement(sql4);
        stmt4.setInt(1 , 6);
        ResultSet rs = stmt4.executeQuery();

        List<Map<String, Object>> resultList = new ArrayList<>();
        while (rs.next()) {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("employee_name", rs.getString("employee_name"));
            resultMap.put("department_name", rs.getString("department_name"));
            resultMap.put("area_name", rs.getString("area_name"));
            resultList.add(resultMap);
        }

        assertThat(resultList)
                .extracting("employee_name", "department_name" , "area_name")
                .containsExactlyInAnyOrder(
                        tuple("Doge Lee", "Research" , "New York")
                );

        close(rs , stmt4 , conn);
    }

    @Test
    void testNoTxWriteAndRead() throws Exception {
        String sql1 = "INSERT INTO area (id, name) VALUES (6, 'Birmingham')";
        Connection conn1 = dataSource.getConnection();
        PreparedStatement stmt1 = conn1.prepareStatement(sql1);

        int row1 = stmt1.executeUpdate();
        close(null , stmt1 , conn1);
        assertThat(row1).isEqualTo(1);

        String sql2 = "SELECT * FROM employee WHERE id = ?";
        Connection conn2 = dataSource.getConnection();
        PreparedStatement stmt2 = conn2.prepareStatement(sql2);
        stmt2.setInt(1 , 1);
        ResultSet rs = stmt2.executeQuery();
        List<Map<String, Object>> resultList = new ArrayList<>();
        while (rs.next()) {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("employee_name", rs.getString("name"));
            resultMap.put("employee_id", rs.getString("id"));
            resultList.add(resultMap);
        }

        assertThat(resultList)
                .extracting("employee_id" , "employee_name")
                .containsExactlyInAnyOrder(
                        tuple("1" , "John Doe")
                );

    }


    private void close(ResultSet rs , PreparedStatement stmt , Connection conn) throws Exception {
        if (Objects.nonNull(rs)) {
            rs.close();
        }
        if (Objects.nonNull(stmt)) {
            stmt.close();
        }
        if (Objects.nonNull(conn)) {
            conn.close();
        }
    }
}