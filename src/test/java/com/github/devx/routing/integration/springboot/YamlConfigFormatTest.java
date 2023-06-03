package com.github.devx.routing.integration.springboot;

import com.github.devx.routing.config.DataSourceConfiguration;
import com.github.devx.routing.config.SqlTypeConfiguration;
import com.github.devx.routing.RoutingTargetType;
import com.github.devx.routing.sql.SqlType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * testing properties config file
 * @author Peng He
 * @since 1.0
 */

@TestPropertySource(locations = "classpath:application.yaml")
@Slf4j
class YamlConfigFormatTest extends SpringBootIntegrationTest {

    @Autowired
    RoutingDataSourceProperties properties;

    @Test
    void testYamlFileDataSourcesConfig() {

        log.info("testing DataSourceConfiguration using spring boot yaml config file");

        assertThat(properties).isNotNull();
        assertThat(properties.getDataSources()).isNotEmpty();
        assertThat(properties.getDataSources()).containsOnlyKeys("write_0" , "read_0" , "read_1");

        assertWrite0();
        assertRead0();
        assertRead1();
    }

    @Test
    void testYamlFileTableRoutingRuleConfig() {

        log.info("testing Table Routing Rule using spring boot yaml config file");

        assertThat(properties.getRules()).isNotNull();
        Map<String, Map<String, SqlTypeConfiguration>> tables = properties.getRules().getTables();
        assertThat(tables).isNotEmpty().containsOnlyKeys("employee");

        for (Map.Entry<String, Map<String, SqlTypeConfiguration>> entry : tables.entrySet()) {
            Map<String, SqlTypeConfiguration> datasourceMap = entry.getValue();
            assertThat(datasourceMap).isNotEmpty();
            assertThat(datasourceMap).containsOnlyKeys("write_0" , "read_0" , "read_1");
            SqlTypeConfiguration write0 = datasourceMap.get("write_0");
            assertThat(write0).isNotNull();
            assertThat(write0.getAllowAllSqlTypes()).isNull();
            assertThat(write0.getSqlTypes()).containsOnly(SqlType.INSERT , SqlType.UPDATE , SqlType.DELETE , SqlType.OTHER);

            SqlTypeConfiguration read0 = datasourceMap.get("read_0");
            assertThat(read0).isNotNull();
            assertThat(read0.getAllowAllSqlTypes()).isNull();
            assertThat(read0.getSqlTypes()).containsOnly(SqlType.SELECT);

            SqlTypeConfiguration read1 = datasourceMap.get("read_1");
            assertThat(read1).isNotNull();
            assertThat(read1.getAllowAllSqlTypes()).isNull();
            assertThat(read1.getSqlTypes()).containsOnly(SqlType.SELECT);
        }
    }

    private void assertWrite0() {
        DataSourceConfiguration write0Configuration = properties.getDataSources().get("write_0");
        assertThat(write0Configuration).isNotNull();
        assertThat(write0Configuration.getType()).isEqualTo(RoutingTargetType.READ_WRITE);
        assertThat(write0Configuration.getDataSourceClass()).isEqualTo("com.zaxxer.hikari.HikariDataSource");
        assertThat(write0Configuration.getWeight()).isEqualTo(99);

        Map<String, Object> write0Props = write0Configuration.getProperties();
        assertThat(write0Props).isNotNull();
        assertThat(write0Props).extracting("jdbcUrl").isEqualTo("jdbc:h2:mem:~/test1;FILE_LOCK=SOCKET;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=TRUE;AUTO_RECONNECT=TRUE;IGNORECASE=TRUE;");
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
        assertThat(read0Configuration.getType()).isEqualTo(RoutingTargetType.READ);
        assertThat(read0Configuration.getDataSourceClass()).isEqualTo("com.zaxxer.hikari.HikariDataSource");
        assertThat(read0Configuration.getWeight()).isEqualTo(6);

        Map<String, Object> read0Props = read0Configuration.getProperties();
        assertThat(read0Props).isNotNull();
        assertThat(read0Props).extracting("jdbcUrl").isEqualTo("jdbc:h2:mem:~/test2;FILE_LOCK=SOCKET;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=TRUE;AUTO_RECONNECT=TRUE;IGNORECASE=TRUE;");
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
        assertThat(read1Configuration.getType()).isEqualTo(RoutingTargetType.READ);
        assertThat(read1Configuration.getDataSourceClass()).isEqualTo("com.zaxxer.hikari.HikariDataSource");
        assertThat(read1Configuration.getWeight()).isEqualTo(10);

        Map<String, Object> read1Props = read1Configuration.getProperties();
        assertThat(read1Props).isNotNull();
        assertThat(read1Props).extracting("jdbcUrl").isEqualTo("jdbc:h2:mem:~/test3;FILE_LOCK=SOCKET;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=TRUE;AUTO_RECONNECT=TRUE;IGNORECASE=TRUE;");
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
