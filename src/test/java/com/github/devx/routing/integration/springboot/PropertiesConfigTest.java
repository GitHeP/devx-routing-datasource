package com.github.devx.routing.integration.springboot;

import com.github.devx.routing.config.DataSourceConfiguration;
import com.github.devx.routing.datasource.DataSourceType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * testing properties config file
 * @author Peng He
 * @since 1.0
 */

@ActiveProfiles("props")
@Slf4j
class PropertiesConfigTest extends SpringBootIntegrationTest {

    @Autowired
    RoutingDataSourceProperties properties;

    @Test
    void testPropertiesFileDataSourcesConfig() {

        log.info("testing DataSourceConfiguration using spring boot properties config file");

        assertThat(properties).isNotNull();
        assertThat(properties.getDataSources()).isNotEmpty();
        assertThat(properties.getDataSources()).containsOnlyKeys("write_0" , "read_0" , "read_1");

        assertWrite0();
        assertRead0();
        assertRead1();
    }

    private void assertWrite0() {
        DataSourceConfiguration write0Configuration = properties.getDataSources().get("write_0");
        assertThat(write0Configuration).isNotNull();
        assertThat(write0Configuration.getType()).isEqualTo(DataSourceType.READ_WRITE);
        assertThat(write0Configuration.getDataSourceClass()).isEqualTo("com.zaxxer.hikari.HikariDataSource");
        assertThat(write0Configuration.getWeight()).isEqualTo(99);

        Map<String, Object> write0Props = write0Configuration.getProperties();
        assertThat(write0Props).isNotNull();
        assertThat(write0Props).extracting("jdbcUrl").isEqualTo("jdbc:h2:mem:~/test1;FILE_LOCK=SOCKET;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=TRUE;AUTO_RECONNECT=TRUE;");
        assertThat(write0Props).extracting("username").isEqualTo("sa");
        assertThat(write0Props).extracting("password").isEqualTo("");
        assertThat(write0Props).extracting("minIdle").isEqualTo("5");
        assertThat(write0Props).extracting("maxPoolSize").isEqualTo("30");
        assertThat(write0Props).extracting("connectionTimeout").isEqualTo("30000");
        assertThat(write0Props).extracting("isAutoCommit").isEqualTo("false");
        assertThat(write0Props).extracting("isReadOnly").isEqualTo("false");

        log.info("write_0 DataSourceConfiguration testing pass {} " , write0Configuration);
    }

    private void assertRead0() {
        DataSourceConfiguration read0Configuration = properties.getDataSources().get("read_0");
        assertThat(read0Configuration).isNotNull();
        assertThat(read0Configuration.getType()).isEqualTo(DataSourceType.READ);
        assertThat(read0Configuration.getDataSourceClass()).isEqualTo("com.zaxxer.hikari.HikariDataSource");
        assertThat(read0Configuration.getWeight()).isEqualTo(6);

        Map<String, Object> read0Props = read0Configuration.getProperties();
        assertThat(read0Props).isNotNull();
        assertThat(read0Props).extracting("jdbcUrl").isEqualTo("jdbc:h2:mem:~/test2;FILE_LOCK=SOCKET;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=TRUE;AUTO_RECONNECT=TRUE;");
        assertThat(read0Props).extracting("username").isEqualTo("sa");
        assertThat(read0Props).extracting("password").isEqualTo("");
        assertThat(read0Props).extracting("minIdle").isEqualTo("10");
        assertThat(read0Props).extracting("maxPoolSize").isEqualTo("30");
        assertThat(read0Props).extracting("connectionTimeout").isEqualTo("40000");
        assertThat(read0Props).extracting("isAutoCommit").isEqualTo("false");
        assertThat(read0Props).extracting("isReadOnly").isEqualTo("true");

        log.info("read_0 DataSourceConfiguration testing pass {} " , read0Configuration);
    }

    private void assertRead1() {
        DataSourceConfiguration read1Configuration = properties.getDataSources().get("read_1");
        assertThat(read1Configuration).isNotNull();
        assertThat(read1Configuration.getType()).isEqualTo(DataSourceType.READ);
        assertThat(read1Configuration.getDataSourceClass()).isEqualTo("com.zaxxer.hikari.HikariDataSource");
        assertThat(read1Configuration.getWeight()).isEqualTo(10);

        Map<String, Object> read1Props = read1Configuration.getProperties();
        assertThat(read1Props).isNotNull();
        assertThat(read1Props).extracting("jdbcUrl").isEqualTo("jdbc:h2:mem:~/test2;FILE_LOCK=SOCKET;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=TRUE;AUTO_RECONNECT=TRUE;");
        assertThat(read1Props).extracting("username").isEqualTo("sa");
        assertThat(read1Props).extracting("password").isEqualTo("");
        assertThat(read1Props).extracting("minIdle").isEqualTo("15");
        assertThat(read1Props).extracting("maxPoolSize").isEqualTo("30");
        assertThat(read1Props).extracting("connectionTimeout").isEqualTo("60000");
        assertThat(read1Props).extracting("isAutoCommit").isEqualTo("false");
        assertThat(read1Props).extracting("isReadOnly").isEqualTo("true");

        log.info("read_1 DataSourceConfiguration testing pass {} " , read1Configuration);
    }

}
