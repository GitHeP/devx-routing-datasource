package com.github.devx.routing.integration.mybatis;

import com.github.devx.routing.integration.springboot.BeforeAfterEachHandleDataTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Peng He
 * @since 1.0
 */

class MyBatisMapperTest extends BeforeAfterEachHandleDataTest {

    @Autowired
    MyBatisMapper mapper;

    @Test
    @DirtiesContext
    void selectEmployeeById() {

        Map<String, Object> employee = mapper.selectEmployeeById(1L);
        assertThat(employee).isNotNull();

        // H2 database changes column names to uppercase
        // I try IGNORECASE=TRUE parameter but not working
        assertThat(employee).extractingByKey("ID").isEqualTo(1L);
        assertThat(employee).extractingByKey("NAME").isEqualTo("John Doe");
        assertThat(employee).extractingByKey("DEPARTMENT_ID").isEqualTo(1);
    }


}
