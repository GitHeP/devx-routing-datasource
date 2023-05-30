package com.github.devx.routing.sql.parser;

import com.github.devx.routing.datasource.DefaultRoutingDataSourceTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
                .output("./JSqlParser_Benchmark.json")
                .build();

        new Runner(opt).run();

    }

    @Test
    void testParseJoin() {

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
        assertThat(statement.getFromTable()).isEqualTo("customers");
        assertThat(statement.getJoinTables()).containsAll(Arrays.asList("orders" , "order_details"));
        assertThat(statement.getSubTables()).isEmpty();
        assertThat(statement.getDatabases()).isEmpty();
    }

    @Test
    @Benchmark
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
        assertThat(statement.getFromTable()).isEqualTo("students");
        assertThat(statement.getJoinTables()).isEmpty();
        assertThat(statement.getSubTables()).contains("students");
        assertThat(statement.getDatabases()).isEmpty();

    }
}