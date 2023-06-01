package com.github.devx.routing.integration.springboot;

import com.github.devx.routing.datasource.RoutingDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author he peng
 * @since 1.0
 */

@ActiveProfiles("props")
@Slf4j
public class DataSourceTest extends SpringBootIntegrationTest {

    @Autowired
    DataSource dataSource;

    @Test
    void testDataSource() {
        log.info("testing DataSource object [{}] isInstanceOf RoutingDataSource" , dataSource);
        assertThat(dataSource).isNotNull().isInstanceOf(RoutingDataSource.class);
    }
}
