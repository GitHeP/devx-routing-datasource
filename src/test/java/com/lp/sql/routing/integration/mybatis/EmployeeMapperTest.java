package com.lp.sql.routing.integration.mybatis;

import com.lp.sql.routing.datasource.RoutingContext;
import com.lp.sql.routing.integration.springboot.BeforeAfterEachHandleDataTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Peng He
 * @since 1.0
 */
class EmployeeMapperTest extends BeforeAfterEachHandleDataTest {

    @Autowired
    EmployeeMapper mapper;

    @Test
    void testInsert() {

        Map<String, Object> row = new HashMap<>();
        row.put("id" , 6);
        row.put("name" , "Peng He");
        row.put("departmentId" , 1);
        int rows = mapper.insert(row);
        assertThat(rows).isEqualTo(1);

        RoutingContext.forceWrite();
        Map<String, Object> result = mapper.selectById(6L);
        assertThat(result).isNotNull()
                .extracting("ID" , "NAME" , "DEPARTMENT_ID")
                .containsExactlyInAnyOrder(6L , "Peng He" , 1);
    }

    @Test
    void testUpdateById() {

        Map<String, Object> row = new HashMap<>();
        row.put("id" , 1);
        row.put("name" , "Peng He");
        row.put("departmentId" , 567);
        int rows = mapper.updateById(row);
        assertThat(rows).isEqualTo(1);

        RoutingContext.forceWrite();
        Map<String, Object> result = mapper.selectById(1L);
        assertThat(result).isNotNull()
                .extracting("ID" , "NAME" , "DEPARTMENT_ID")
                .containsExactlyInAnyOrder(1L , "Peng He" , 567);
    }

    @Test
    void testDelete() {

        int rows = mapper.deleteById(1L);
        assertThat(rows).isEqualTo(1);
        RoutingContext.forceWrite();
        Map<String, Object> result = mapper.selectById(1L);
        assertThat(result).isNull();
    }

    @Test
    void testSelectById() {

        Map<String, Object> result = mapper.selectById(1L);
        assertThat(result).isNotNull()
                .extracting("ID" , "NAME" , "DEPARTMENT_ID")
                .containsExactlyInAnyOrder(1L , "John Doe" , 1);
    }
}
