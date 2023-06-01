package com.github.devx.routing.integration.springboot;

import com.github.devx.routing.datasource.RoutingContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * testing using JdbcTemplate
 *
 * @author Peng He
 * @since 1.0
 */
class JdbcTemplateTest extends BeforeAfterEachHandleDataTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void testSelect_1() {

        String sql = "select * from employee where id = ?";
        Map<String, Object> result = jdbcTemplate.queryForObject(sql , new Object[] {1} ,new ColumnMapRowMapper());
        assertThat(result).extractingByKey("ID").isEqualTo(1);
        assertThat(result).extractingByKey("NAME").isEqualTo("John Doe");
        assertThat(result).extractingByKey("DEPARTMENT_ID").isEqualTo(1);
        assertThat(RoutingContext.getRoutedDataSourceName()).containsAnyOf("read_0" , "read_1");
    }
}
