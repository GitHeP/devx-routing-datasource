package com.github.devx.routing.sql.parser;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author he peng
 * @since 1.0
 *
 * @see JSqlParser
 */
class JSqlParserTest {

    static SqlParser sqlParser = new JSqlParser();

    @BeforeAll
    static void init() {

    }

    @Test
    void testParse_1() {

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
}