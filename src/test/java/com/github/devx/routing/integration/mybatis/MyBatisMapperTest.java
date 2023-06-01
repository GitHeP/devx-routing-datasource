package com.github.devx.routing.integration.mybatis;

import com.github.devx.routing.datasource.RoutingContext;
import com.github.devx.routing.integration.springboot.SpringBootIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Peng He
 * @since 1.0
 */

class MyBatisMapperTest extends SpringBootIntegrationTest {

    @Autowired
    MyBatisMapper mapper;

    @Sql(scripts = {"/init.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    void selectEmployeeById() {

        RoutingContext.force("write_0");
        Map<String, Object> employee = mapper.selectEmployeeById(1L);
        assertThat(employee).isNotNull();
        assertThat(employee).extractingByKey("id").isEqualTo(1);
        assertThat(employee).extractingByKey("name").isEqualTo("DevX");
        assertThat(employee).extractingByKey("department_id").isEqualTo(1);
    }
}
