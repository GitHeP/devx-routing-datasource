package com.github.devx.routing.integration.springboot;

import com.github.devx.routing.datasource.RoutingDataSource;
import com.github.devx.routing.jdbc.RoutingConnection;
import com.github.devx.routing.jdbc.RoutingContextClearPreparedStatement;
import com.github.devx.routing.jdbc.RoutingStatement;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

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

    @Test
    void testGetConnection() throws Exception {

        log.info("testing DataSource get connection isInstanceOf RoutingConnection");
        Connection connection = dataSource.getConnection();
        assertThat(connection).isNotNull().isInstanceOf(RoutingConnection.class);
        connection.close();
    }

    @Test
    void testPreparedStatement() throws Exception {
        log.info("testing PreparedStatement isInstanceOf RoutingContextClearPreparedStatement");
        Connection connection = dataSource.getConnection();
        assertThat(connection).isNotNull().isInstanceOf(RoutingConnection.class);
        PreparedStatement prepareStatement = connection.prepareStatement("show databases");
        assertThat(prepareStatement).isNotNull().isInstanceOf(RoutingContextClearPreparedStatement.class);
        prepareStatement.close();
        connection.close();
    }

    @Test
    void testStatement() throws Exception {
        log.info("testing Statement isInstanceOf RoutingStatement");
        Connection connection = dataSource.getConnection();
        assertThat(connection).isNotNull().isInstanceOf(RoutingConnection.class);
        Statement statement = connection.createStatement();
        assertThat(statement).isNotNull().isInstanceOf(RoutingStatement.class);
        statement.close();
        connection.close();
    }
}
