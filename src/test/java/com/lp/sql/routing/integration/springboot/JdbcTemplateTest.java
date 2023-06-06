package com.lp.sql.routing.integration.springboot;

import com.lp.sql.routing.RoutingContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        assertThat(result).isNotNull().extractingByKey("ID").isEqualTo(1L);
        assertThat(result).extractingByKey("NAME").isEqualTo("John Doe");
        assertThat(result).extractingByKey("DEPARTMENT_ID").isEqualTo(1);
    }

    @Test
    void testInsert() {

        String sql = "insert into employee (id , name , department_id) values (? , ? , ?)";
        int row = jdbcTemplate.update(sql, ps -> {
            ps.setLong(1 , 5L);
            ps.setString(2 , "Peng He");
            ps.setInt(3 , 1);
        });
        assertThat(row).isEqualTo(1);

        RoutingContext.forceWrite();
        Map<String, Object> result = jdbcTemplate.queryForObject("select * from employee where id = ?" , new Object[] {5L} ,new ColumnMapRowMapper());
        assertThat(result).isNotNull().extractingByKey("ID").isEqualTo(5L);
        assertThat(result).extractingByKey("NAME").isEqualTo("Peng He");
        assertThat(result).extractingByKey("DEPARTMENT_ID").isEqualTo(1);
    }

    @Test
    void testUpdate() {
        String sql = "update employee set name = ? , department_id = ? where id = ?";
        int row = jdbcTemplate.update(sql, ps -> {
            ps.setString(1 , "Peng He");
            ps.setLong(2 , 6666);
            ps.setInt(3 , 1);
        });
        assertThat(row).isEqualTo(1);

        RoutingContext.forceWrite();
        Map<String, Object> result = jdbcTemplate.queryForObject("select * from employee where id = ?" , new Object[] {1L} ,new ColumnMapRowMapper());
        assertThat(result).isNotNull().extractingByKey("ID").isEqualTo(1L);
        assertThat(result).extractingByKey("NAME").isEqualTo("Peng He");
        assertThat(result).extractingByKey("DEPARTMENT_ID").isEqualTo(6666);
    }

    @Test
    void testDelete() {
        String sql = "delete from employee where id = ?";
        int row = jdbcTemplate.update(sql, 1L);
        assertThat(row).isEqualTo(1);
        RoutingContext.forceWrite();

        assertThatThrownBy(() -> {
            Map<String, Object> result = jdbcTemplate.queryForObject("select * from employee where id = ?" , new Object[] {1L} ,new ColumnMapRowMapper());
            assertThat(result).isNull();
        }).isInstanceOf(EmptyResultDataAccessException.class);
    }
}
