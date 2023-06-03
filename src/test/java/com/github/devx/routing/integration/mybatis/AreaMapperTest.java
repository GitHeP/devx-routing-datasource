package com.github.devx.routing.integration.mybatis;

import com.github.devx.routing.datasource.RoutingContext;
import com.github.devx.routing.integration.springboot.BeforeAfterEachHandleDataTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Peng He
 * @since 1.0
 */
class AreaMapperTest extends BeforeAfterEachHandleDataTest {

    @Autowired
    AreaMapper mapper;

    @Test
    void testInsert() {

        Map<String, Object> row = new HashMap<>();
        row.put("id" , 6);
        row.put("name" , "Paris");
        int rows = mapper.insert(row);
        assertThat(rows).isEqualTo(1);

        RoutingContext.forceWrite();
        Map<String, Object> area = mapper.selectById(6L);
        assertThat(area).isNotNull()
                .extracting("ID" , "NAME")
                .containsExactlyInAnyOrder(6L , "Paris");
    }

    @Test
    void testUpdateById() {

        Map<String, Object> row = new HashMap<>();
        row.put("id" , 1);
        row.put("name" , "Tokyo");
        int rows = mapper.updateById(row);
        assertThat(rows).isEqualTo(1);

        RoutingContext.forceWrite();
        Map<String, Object> area = mapper.selectById(1L);
        assertThat(area).isNotNull()
                .extracting("ID" , "NAME")
                .containsExactlyInAnyOrder(1L , "Tokyo");
    }

    @Test
    void testDelete() {

        int rows = mapper.deleteById(1L);
        assertThat(rows).isEqualTo(1);
        RoutingContext.forceWrite();
        Map<String, Object> area = mapper.selectById(1L);
        assertThat(area).isNull();
    }

    @Test
    void testSelectById() {

        Map<String, Object> area = mapper.selectById(1L);
        assertThat(area).isNotNull()
                .extracting("ID" , "NAME")
                .containsExactlyInAnyOrder(1L , "East");
    }
}
