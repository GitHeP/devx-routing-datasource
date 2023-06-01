package com.github.devx.routing.integration.mybatis;

import com.github.devx.routing.datasource.RoutingContext;
import com.github.devx.routing.integration.springboot.InitDataTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Peng He
 * @since 1.0
 */

class MyBatisMapperTest extends InitDataTest {

    @Autowired
    MyBatisMapper mapper;

    @Test
    @DirtiesContext
    void selectEmployeeById() {

        Map<String, Object> employee = mapper.selectEmployeeById(1L);
        assertThat(employee).isNotNull();
        assertThat(employee).extractingByKey("id").isEqualTo(1);
        assertThat(employee).extractingByKey("name").isEqualTo("John Doe");
        assertThat(employee).extractingByKey("department_id").isEqualTo(1);
        assertThat(RoutingContext.getRoutedDataSourceName()).containsAnyOf("read_0" , "read_1");
        // TODO H2 Database IGNORECASE=TRUE not working
    }


}
