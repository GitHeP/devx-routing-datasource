package com.lp.sql.routing.integration.springboot;

import com.lp.sql.routing.datasource.RoutingDataSource;
import com.lp.sql.routing.jdbc.RoutingConnection;
import com.lp.sql.routing.jdbc.RoutingContextClearPreparedStatement;
import com.lp.sql.routing.jdbc.RoutingStatement;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Peng He
 * @since 1.0
 */

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
