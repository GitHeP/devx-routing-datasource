package com.github.devx.routing.sql.parser;

import com.github.devx.routing.sql.SqlStatementType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author he peng
 * @since 1.0
 *
 * @see JSqlParser
 */

@State(Scope.Benchmark)
@Slf4j
public class JSqlParserTest {

    static SqlParser sqlParser = new JSqlParser();

    @BeforeAll
    static void init() {

    }

    @Test
    public void testBenchmark() throws Exception {
        Options opt = new OptionsBuilder()
                .include(JSqlParserTest.class.getSimpleName())
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.NANOSECONDS)
                .forks(0)
                //.threads(Runtime.getRuntime().availableProcessors() * 16)
                .threads(1)
                .syncIterations(true)
                .shouldFailOnError(true)
                .shouldDoGC(false)
                .verbosity(VerboseMode.EXTRA)
                .resultFormat(ResultFormatType.JSON)
                .output("./JSqlParser_Benchmark.json")
                .build();

        new Runner(opt).run();

    }

    @Benchmark
    @Warmup(iterations = 5, time = 5 , timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = 100, time = 5 , timeUnit = TimeUnit.MILLISECONDS)
    @CompilerControl(CompilerControl.Mode.INLINE)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Test
    public void testParseJoin() {

        log.info("testing parse join select sql");

        String sql = "SELECT customers.customer_name, orders.order_id, order_details.product_name, order_details.unit_price, " +
                "order_details.quantity, (order_details.unit_price * order_details.quantity) AS total_price\n" +
                "FROM customers\n" +
                "INNER JOIN orders\n" +
                "ON customers.customer_id = orders.customer_id\n" +
                "INNER JOIN order_details\n" +
                "ON orders.order_id = order_details.order_id\n" +
                "WHERE customers.country = 'USA'\n" +
                "AND orders.order_date BETWEEN '2022-01-01' AND '2022-12-31'\n" +
                "AND order_details.quantity > 10\n" +
                "ORDER BY customers.customer_name ASC, orders.order_date DESC;\n";

        SqlStatement statement = sqlParser.parse(sql);
        assertThat(statement).isNotNull();
        assertThat(statement.isWrite()).isFalse();
        assertThat(statement.isRead()).isTrue();
        assertThat(statement.getNormalTables()).contains("customers");
        assertThat(statement.getJoinTables()).containsAll(Arrays.asList("orders" , "order_details"));
        assertThat(statement.getSubTables()).isEmpty();
        assertThat(statement.getDatabases()).isEmpty();
    }

    @Test
    public void testParseSubSelect() {

        log.info("testing parse sub select sql");

        String sql = "SELECT s1.student_name, s1.student_id, s1.grade\n" +
                "FROM students s1\n" +
                "INNER JOIN (\n" +
                "    SELECT AVG(grade) AS avg_grade\n" +
                "    FROM students\n" +
                "    WHERE grade >= 60\n" +
                ") s2\n" +
                "ON s1.grade >= s2.avg_grade\n" +
                "WHERE s1.gender = 'F'\n" +
                "ORDER BY s1.grade DESC;\n";

        SqlStatement statement = sqlParser.parse(sql);
        assertThat(statement).isNotNull();
        assertThat(statement.isWrite()).isFalse();
        assertThat(statement.isRead()).isTrue();
        assertThat(statement.getNormalTables()).contains("students");
        assertThat(statement.getJoinTables()).isEmpty();
        assertThat(statement.getSubTables()).contains("students");
        assertThat(statement.getDatabases()).isEmpty();

    }

    @Test
    public void testParseUnion() {

        log.info("testing parse union select sql");

        String sql = "select\n" +
                "        *,\n" +
                "        (select GROUP_CONCAT(sku) from py.p_tradedt where tradenid = t.nid) skuList\n" +
                "        from py.p_tradewait t\n" +
                "        where t.ordertime >=  DATE_FORMAT('2023-05-26 00:00:00.241','%Y-%m-%d %H:%i:%s') and\n" +
                "            DATE_FORMAT('2023-05-26 23:59:59.241','%Y-%m-%d %H:%i:%s') >= t.ordertime\n" +
                "        union all\n" +
                "        select\n" +
                "        *,\n" +
                "        (select GROUP_CONCAT(sku) from py.p_tradedt where tradenid = t.nid) skuList\n" +
                "        from py.p_tradesend t\n" +
                "        where t.ordertime >=  DATE_FORMAT('2023-05-26 00:00:00.241','%Y-%m-%d %H:%i:%s') and\n" +
                "            DATE_FORMAT('2023-05-26 23:59:59.241','%Y-%m-%d %H:%i:%s') >= t.ordertime\n" +
                "        union all\n" +
                "        select\n" +
                "        *,\n" +
                "        (select GROUP_CONCAT(sku) from py.p_tradedt where tradenid = t.nid) skuList\n" +
                "        from py.p_tradestock t\n" +
                "        where t.ordertime >=  DATE_FORMAT('2023-05-26 00:00:00.241','%Y-%m-%d %H:%i:%s') and\n" +
                "            DATE_FORMAT('2023-05-26 23:59:59.241','%Y-%m-%d %H:%i:%s') >= t.ordertime\n" +
                "        union all\n" +
                "        select\n" +
                "        *,\n" +
                "        (select GROUP_CONCAT(sku) from py.p_tradedt where tradenid = t.nid) skuList\n" +
                "        from py.p_trade t\n" +
                "        where t.ordertime >=  DATE_FORMAT('2023-05-26 00:00:00.241','%Y-%m-%d %H:%i:%s') and\n" +
                "            DATE_FORMAT('2023-05-26 23:59:59.241','%Y-%m-%d %H:%i:%s') >= t.ordertime";

        SqlStatement statement = sqlParser.parse(sql);
        assertThat(statement).isNotNull();
        assertThat(statement.isWrite()).isFalse();
        assertThat(statement.isRead()).isTrue();
        assertThat(statement.getNormalTables()).containsAll(Arrays.asList("p_tradewait" , "p_tradesend" , "p_tradestock" , "p_trade"));
        assertThat(statement.getTables()).containsAll(Arrays.asList("p_tradedt" , "p_tradewait" , "p_tradesend" , "p_tradestock" , "p_trade"));
        assertThat(statement.getJoinTables()).isEmpty();
        assertThat(statement.getSubTables()).contains("p_tradedt");
        assertThat(statement.getDatabases()).contains("py");
    }

    @Test
    public void testParseJoinSubSelect() {

        log.info("testing parse join sub select sql");

        String sql = "SELECT\n" +
                "        'North America' as 'auth_region',\n" +
                "        t1.request_report_type,\n" +
                "        t1.num  'allnum',\n" +
                "        t2.num  'errorNum',\n" +
                "        t2.num / t1.num 'rate'\n" +
                "        FROM\n" +
                "        ( SELECT request_report_type, count(1) num FROM t_amazon_report_log WHERE\n" +
                "        created_time>DATE_SUB(now(),INTERVAL 3 hour) and created_time <= DATE_SUB(now(),INTERVAL 1 hour) and auth_region=1\n" +
                "        GROUP BY request_report_type ) t1\n" +
                "        LEFT JOIN (\n" +
                "        SELECT\n" +
                "        request_report_type,\n" +
                "        count( 1 ) num\n" +
                "        FROM\n" +
                "        t_amazon_report_log\n" +
                "        WHERE\n" +
                "        IFNULL( report_id, '' ) = ''\n" +
                "        AND created_time>DATE_SUB(now(),INTERVAL 3 hour) and created_time <= DATE_SUB(now(),INTERVAL 1 hour) and auth_region=1 and processing_status in(0,2,4,5,6,7)\n" +
                "        GROUP BY\n" +
                "        request_report_type\n" +
                "        ) t2 ON t1.request_report_type = t2.request_report_type\n" +
                "\n" +
                "        UNION all\n" +
                "\n" +
                "        SELECT\n" +
                "        'Europe'  as 'auth_region',\n" +
                "        t1.request_report_type,\n" +
                "        t1.num  'allnum',\n" +
                "        t2.num  'errorNum',\n" +
                "        t2.num / t1.num 'rate'\n" +
                "        FROM\n" +
                "        ( SELECT request_report_type, count(1) num FROM t_amazon_report_log WHERE\n" +
                "        created_time>DATE_SUB(now(),INTERVAL 3 hour) and created_time <= DATE_SUB(now(),INTERVAL 1 hour) and auth_region=2\n" +
                "        GROUP BY request_report_type ) t1\n" +
                "        LEFT JOIN (\n" +
                "        SELECT\n" +
                "        request_report_type,\n" +
                "        count( 1 ) num\n" +
                "        FROM\n" +
                "        t_amazon_report_log\n" +
                "        WHERE\n" +
                "        IFNULL( report_id, '' ) = ''\n" +
                "        AND created_time>DATE_SUB(now(),INTERVAL 3 hour) and created_time <= DATE_SUB(now(),INTERVAL 1 hour) and auth_region=2 and processing_status in(0,2,4,5,6,7)\n" +
                "        GROUP BY\n" +
                "        request_report_type\n" +
                "        ) t2 ON t1.request_report_type = t2.request_report_type";

        SqlStatement statement = sqlParser.parse(sql);
        assertThat(statement).isNotNull();
        assertThat(statement.isWrite()).isFalse();
        assertThat(statement.isRead()).isTrue();
        assertThat(statement.getNormalTables()).isEmpty();
        assertThat(statement.getTables()).containsAll(Collections.singletonList("t_amazon_report_log"));
        assertThat(statement.getJoinTables()).isEmpty();
        assertThat(statement.getSubTables()).contains("t_amazon_report_log");
        assertThat(statement.getDatabases()).isEmpty();
    }

    @Test
    public void testParseMultiJoin() {

        log.info("testing parse multi join select sql");

        String sql = "SELECT\n" +
                "        ub.team_name as teamName,\n" +
                "        ub.team_id as teamId,\n" +
                "        ub.company_name as companyName,\n" +
                "        ub.company_id as companyId,\n" +
                "        ub.name as userName,\n" +
                "        b.created_by as userId,\n" +
                "        b.purchase_number as purchaseNumber,\n" +
                "        b.purchase_status as purchaseStatus,\n" +
                "        DATE_FORMAT(b.created_time,'%Y-%m-%d') as createDay,\n" +
                "        pg.CostPrice as costPrice,\n" +
                "        s.tax_price as taxPrice\n" +
                "        FROM\n" +
                "        t_buy_purchase_demand b\n" +
                "        left join t_py_goods pg on b.sku=pg.sku\n" +
                "        left join  t_user_baseinfo ub on b.created_by = ub.id\n" +
                "        LEFT JOIN   t_buy_stock_order a  ON a.id = b.purchase_order_id\n" +
                "        left join t_buy_stock_order_details s on a.id=s.order_id and b.sku=s.sku\n" +
                "        WHERE\n" +
                "        1=1\n" +
                "        and b.has_file = 0\n" +
                "        and b.created_time >= concat( '2023-05-12',' 00:00:00')\n" +
                "        and b.created_time <= concat( '2023-05-27',' 23:59:59')\n" +
                "        and ub.department_name = 'Amazon'\n" +
                "\t    and b.order_platform not like concat('%','sampling','%')\n" +
                "        and b.purchase_status in\n" +
                "         (2 , 4 , 5 , 676 , 3423 , 3546 , 600 , 2545)";

        SqlStatement statement = sqlParser.parse(sql);
        assertThat(statement).isNotNull();
        assertThat(statement.isWrite()).isFalse();
        assertThat(statement.isRead()).isTrue();
        assertThat(statement.getNormalTables()).containsAll(Collections.singletonList("t_buy_purchase_demand"));
        assertThat(statement.getTables()).containsAll(Arrays.asList("t_buy_purchase_demand" , "t_py_goods" , "t_user_baseinfo" , "t_buy_stock_order" , "t_buy_stock_order_details"));
        assertThat(statement.getJoinTables()).containsAll(Arrays.asList("t_py_goods" , "t_user_baseinfo" , "t_buy_stock_order" , "t_buy_stock_order_details"));
        assertThat(statement.getSubTables()).isEmpty();
        assertThat(statement.getDatabases()).isEmpty();
    }

    @Test
    public void testParseInsert() {

        String sql = "INSERT INTO table1 (column1, column2, column3)\n" +
                "VALUES ('value1', 'value2', (SELECT id FROM table2 WHERE name='value3'));";

        SqlStatement statement = sqlParser.parse(sql);
        assertThat(statement).isNotNull();
        assertThat(statement.isWrite()).isTrue();
        assertThat(statement.isRead()).isFalse();
        assertThat(statement.getTables()).containsAll(Arrays.asList("table1" , "table2"));
        assertThat(statement.getNormalTables()).containsAll(Collections.singletonList("table1"));
        assertThat(statement.getJoinTables()).isEmpty();
        assertThat(statement.getSubTables()).containsAll(Collections.singletonList("table2"));
        assertThat(statement.getStatementType()).isEqualTo(SqlStatementType.INSERT);
    }
}