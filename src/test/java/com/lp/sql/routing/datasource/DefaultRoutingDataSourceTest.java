package com.lp.sql.routing.datasource;

import com.lp.sql.routing.RoutingTargetType;
import com.lp.sql.routing.config.DataSourceConfiguration;
import com.lp.sql.routing.config.RoutingConfiguration;
import com.lp.sql.routing.loadbalance.ReadLoadBalanceType;
import com.lp.sql.routing.loadbalance.WriteLoadBalanceType;
import com.lp.sql.routing.rule.group.EmbeddedRoutingGroup;
import com.lp.sql.routing.rule.group.CompositeRoutingGroup;
import com.lp.sql.routing.sql.parser.JSqlParser;
import com.lp.sql.routing.sql.parser.SqlParser;
import com.lp.sql.routing.util.RoutingUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import javax.sql.DataSource;
import java.io.FileReader;
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
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;




/**
 * include unit test and Benchmark test
 *
 * @author Peng He
 * @since 1.0
 *
 * @see DefaultRoutingDataSource
 */

@State(Scope.Benchmark)
@Slf4j
public class DefaultRoutingDataSourceTest {

    static DataSource dataSource;

    static String writeDataSourceName = "write";
    static String readDataSource0Name = "read0";
    static String readDataSource1Name = "read1";

    static Random random = new Random();

    @BeforeAll
    public static void initDataSource() throws Exception {

        String initSqlPath = "src/test/resources/init.sql";

        HikariConfig config1 = new HikariConfig();
        // TRACE_LEVEL_SYSTEM_OUT=3;
        config1.setJdbcUrl("jdbc:h2:mem:~/test1;FILE_LOCK=SOCKET;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=TRUE;AUTO_RECONNECT=TRUE;");
        config1.setDriverClassName("org.h2.Driver");
        config1.setUsername("sa");
        config1.setPassword("");
        config1.setMinimumIdle(5);
        config1.setMaximumPoolSize(30);
        config1.setConnectionTimeout(30000);
        config1.setAutoCommit(false);

        HikariDataSource writeDataSource = new HikariDataSource(config1);

        Connection conn1 = writeDataSource.getConnection();
        ResultSet rs1 = RunScript.execute(conn1 , new FileReader(initSqlPath));
        close(rs1 , null , conn1);

        HikariConfig config2 = new HikariConfig();
        config2.setJdbcUrl("jdbc:h2:mem:~/test2;FILE_LOCK=SOCKET;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=TRUE;");
        config2.setDriverClassName("org.h2.Driver");
        config2.setUsername("sa");
        config2.setPassword("");
        config2.setMinimumIdle(5);
        config2.setMaximumPoolSize(30);
        config2.setConnectionTimeout(30000);
        config2.setAutoCommit(false);

        HikariDataSource readDataSource0 = new HikariDataSource(config2);

        Connection conn2 = readDataSource0.getConnection();
        ResultSet rs2 = RunScript.execute(conn2, new FileReader(initSqlPath));
        close(rs2 , null , conn2);

        HikariConfig config3 = new HikariConfig();
        config3.setJdbcUrl("jdbc:h2:mem:~/test3;FILE_LOCK=SOCKET;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=TRUE;");
        config3.setDriverClassName("org.h2.Driver");
        config3.setUsername("sa");
        config3.setPassword("");
        config3.setMinimumIdle(5);
        config3.setMaximumPoolSize(30);
        config3.setConnectionTimeout(30000);
        config3.setAutoCommit(false);

        HikariDataSource readDataSource1 = new HikariDataSource(config3);

        Connection conn3 = readDataSource1.getConnection();
        ResultSet rs3 = RunScript.execute(conn3, new FileReader(initSqlPath));
        close(rs3 , null , conn3);


        SqlParser sqlParser = new JSqlParser();

        CompositeRoutingGroup compositeRoutingGroup = new CompositeRoutingGroup();
        RoutingConfiguration conf = new RoutingConfiguration();

        DataSourceConfiguration writeDSConf = new DataSourceConfiguration();
        writeDSConf.setDataSourceClass(HikariDataSource.class.getCanonicalName());
        writeDSConf.setName(writeDataSourceName);
        writeDSConf.setType(RoutingTargetType.READ_WRITE);
        writeDSConf.setWeight(1);

        DataSourceConfiguration read0DSConf = new DataSourceConfiguration();
        read0DSConf.setDataSourceClass(HikariDataSource.class.getCanonicalName());
        read0DSConf.setName(readDataSource0Name);
        read0DSConf.setType(RoutingTargetType.READ);
        read0DSConf.setWeight(1);

        DataSourceConfiguration read1DSConf = new DataSourceConfiguration();
        read1DSConf.setDataSourceClass(HikariDataSource.class.getCanonicalName());
        read1DSConf.setName(readDataSource0Name);
        read1DSConf.setType(RoutingTargetType.READ);
        read1DSConf.setWeight(1);


        Map<String, DataSourceConfiguration> dataSourcesConf = new HashMap<>();
        dataSourcesConf.put(writeDataSourceName , writeDSConf);
        dataSourcesConf.put(readDataSource0Name , read0DSConf);
        dataSourcesConf.put(readDataSource1Name , read1DSConf);


        //conf.setDataSources(dataSourcesConf);
        Set<String> masters = new HashSet<>();
        masters.add(writeDataSourceName);
        conf.setMasters(masters);
        Set<String> replicas = new HashSet<>();
        replicas.add(readDataSource0Name);
        replicas.add(readDataSource1Name);
        conf.setReplicas(replicas);
        conf.setReadLoadBalanceType(ReadLoadBalanceType.WEIGHT_RANDOM_BALANCE_ONLY_READ);
        conf.setWriteLoadBalanceType(WriteLoadBalanceType.RANDOM_BALANCE_READ_WRITE);
        conf.setRules(null);
        EmbeddedRoutingGroup embeddedRoutingGroup = new EmbeddedRoutingGroup(conf , sqlParser);
        compositeRoutingGroup.installLast(embeddedRoutingGroup);

        RoutingKeyProvider routingKeyProvider = new RoutingContextRoutingKeyProvider();

        Map<String, DataSource> dataSources = new HashMap<>();
        dataSources.put(writeDataSourceName , new DataSourceWrapper(writeDataSource , new DataSourceAttribute(RoutingTargetType.READ_WRITE, writeDataSourceName, 1)));
        dataSources.put(readDataSource0Name , new DataSourceWrapper(readDataSource0 , new DataSourceAttribute(RoutingTargetType.READ, readDataSource0Name, 1)));
        dataSources.put(readDataSource1Name , new DataSourceWrapper(readDataSource1 , new DataSourceAttribute(RoutingTargetType.READ, readDataSource1Name, 1)));


        dataSource = new DefaultRoutingDataSource(dataSources , compositeRoutingGroup , routingKeyProvider);
    }

    @TearDown
    @AfterAll
    public static void clearDataBase() throws Exception {

    }

    //@AfterEach
    void clearData() throws Exception {

        //RoutingContext.force(writeDataSourceName);
        //Connection conn1 = dataSource.getConnection();
        //conn1.setAutoCommit(true);
        //Statement stmt1 = conn1.createStatement();
        //stmt1.execute("DROP ALL OBJECTS");
        //close(null , stmt1 , conn1);
        //
        //RoutingContext.force(readDataSource0Name);
        //Connection conn2 = dataSource.getConnection();
        //conn2.setAutoCommit(true);
        //
        //Statement stmt2 = conn2.createStatement();
        //stmt2.execute("DROP ALL OBJECTS");
        //close(null , stmt2 , conn2);
        //
        //RoutingContext.force(readDataSource1Name);
        //Connection conn3 = dataSource.getConnection();
        //conn3.setAutoCommit(true);
        //
        //Statement stmt3 = conn3.createStatement();
        //stmt1.execute("DROP ALL OBJECTS");
        //close(null , stmt3 , conn3);
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
        Assertions.assertThat(RoutingUtils.isRoutingWrite(conn1)).isTrue();

        int row1 = stmt1.executeUpdate();
        close(null , stmt1 , conn1);
        assertThat(row1).isEqualTo(1);

        String sql2 = "SELECT * FROM employee WHERE id = ?";
        Connection conn2 = dataSource.getConnection();
        PreparedStatement stmt2 = conn2.prepareStatement(sql2);
        assertThat(RoutingUtils.isRoutingRead(conn2)).isTrue();
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

    @Test
    void testTxReadonly() throws Exception {

        Connection conn = dataSource.getConnection();
        conn.setAutoCommit(false);
        conn.setReadOnly(true);

        String sql1 = "SELECT * FROM employee WHERE id = ?";
        PreparedStatement stmt1 = conn.prepareStatement(sql1);
        stmt1.setInt(1 , 1);

        assertThat(RoutingUtils.isRoutingRead(conn)).isTrue();

        ResultSet rs = stmt1.executeQuery();
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

    @Test
    void testTxReadonlyNegative() throws Exception {
        Connection conn = dataSource.getConnection();
        conn.setAutoCommit(false);
        conn.setReadOnly(true);

        String sql1 = "INSERT INTO area (id, name) VALUES (6, 'Manchester')";
        PreparedStatement stmt1 = conn.prepareStatement(sql1);

        assertThat(RoutingUtils.isRoutingRead(conn)).isTrue();

        int rows = stmt1.executeUpdate();
        assertThat(rows).isEqualTo(1);
        close(null , stmt1 , conn);
    }

    @Test
    void testForceRoutingWriteDataSource() throws Exception {

        RoutingContext.forceWrite();
        Connection conn = dataSource.getConnection();
        String sql1 = "SELECT * FROM employee WHERE id = ?";
        PreparedStatement stmt1 = conn.prepareStatement(sql1);
        stmt1.setInt(1 , 1);

        assertThat(RoutingUtils.isRoutingWrite(conn)).isTrue();

        ResultSet rs = stmt1.executeQuery();
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

    @Test
    void testConcurrentBehavior() throws Exception {
        int threads = 20;
        CyclicBarrier startBarrier = new CyclicBarrier(threads);
        List<Callable<List<Integer>>> tasks = new ArrayList<>();
        AtomicInteger count = new AtomicInteger(10);
        for (int i = 0; i < threads; i++) {
            tasks.add(() -> {
                List<Integer> ids = new ArrayList<>();
                for (int j = 0; j < 10; j++) {
                    Connection conn = dataSource.getConnection();
                    conn.setAutoCommit(true);
                    conn.setReadOnly(false);

                    String sql1 = "INSERT INTO area (id, name) VALUES (? , 'Manchester')";
                    PreparedStatement stmt1 = conn.prepareStatement(sql1);
                    int id = count.incrementAndGet();
                    stmt1.setInt(1 , id);
                    assertThat(RoutingUtils.isRoutingWrite(conn)).isTrue();
                    int rows = stmt1.executeUpdate();
                    assertThat(rows).isEqualTo(1);
                    close(null , stmt1 , conn);
                    ids.add(id);
                }
                startBarrier.await();
                return ids;
            });
        }

        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        List<Future<List<Integer>>> futures = executorService.invokeAll(tasks);
        List<Integer> idList = new ArrayList<>();
        for (Future<List<Integer>> future : futures) {
            idList.addAll(future.get());
        }

        executorService.shutdown();

        RoutingContext.force(writeDataSourceName);
        Connection conn = dataSource.getConnection();
        ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM area");
        List<Map<String, Object>> resultList = new ArrayList<>();
        while (rs.next()) {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("id", Integer.parseInt(rs.getString("id")));
            resultMap.put("name", rs.getString("name"));
            resultList.add(resultMap);
        }

        assertThat(resultList)
                .extracting("id")
                .containsAll(idList);
    }

    @Test
    public void testBenchmark() throws Exception {
        Options opt = new OptionsBuilder()
                .include(DefaultRoutingDataSourceTest.class.getSimpleName())
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.NANOSECONDS)
                //.warmupIterations(5)
                //.measurementIterations(5)
                //.measurementTime(TimeValue.minutes(1))
                .forks(0)
                //.threads(Runtime.getRuntime().availableProcessors() * 16)
                .threads(1)
                .syncIterations(true)
                .shouldFailOnError(true)
                .shouldDoGC(false)
                .verbosity(VerboseMode.EXTRA)
                .resultFormat(ResultFormatType.JSON)
                .output("./DefaultRoutingDataSourceBenchmark.json")
                .build();

        new Runner(opt).run();

    }

    @Benchmark
    @Warmup(iterations = 10, time = 1 , timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 100, time = 1 , timeUnit = TimeUnit.SECONDS)
    @CompilerControl(CompilerControl.Mode.INLINE)
    public int testInsertBenchmark() throws Exception {

        long id = System.nanoTime() + random.nextLong();
        String sql = "INSERT INTO area (id, name) VALUES (?, 'New York')";
        log.info("testInsertBenchmark sql [INSERT INTO area (id, name) VALUES ({}, 'New York')]" , id);
        Connection conn = dataSource.getConnection();
        conn.setAutoCommit(true);
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setLong(1 , id);

        int row = stmt.executeUpdate();
        assertThat(row).isEqualTo(1);
        close(null , stmt , conn);
        return row;
    }

    private static void close(ResultSet rs , Statement stmt , Connection conn) throws Exception {
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