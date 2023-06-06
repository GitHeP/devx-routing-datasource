package com.lp.sql.routing.integration.springboot;

import com.lp.sql.routing.RoutingContext;
import com.lp.sql.routing.datasource.RoutingDataSource;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Objects;

/**
 * @author Peng He
 * @since 1.0
 */

@TestPropertySource(locations = "classpath:application.yaml")
public class BeforeAfterEachHandleDataTest extends SpringBootIntegrationTest {

    @Autowired
    RoutingDataSource dataSource;

    @BeforeEach
    protected void initData() throws Exception {

        String initSqlPath = "src/test/resources/init.sql";

        DataSource write0 = dataSource.getDataSourceWithName("write_0");

        Connection write0Conn = write0.getConnection();
        ResultSet write0Rs = RunScript.execute(write0Conn , new FileReader(initSqlPath));
        close(write0Rs , null , write0Conn);

        DataSource read0 = dataSource.getDataSourceWithName("read_0");

        Connection read0Conn = read0.getConnection();
        ResultSet read0Rs = RunScript.execute(read0Conn , new FileReader(initSqlPath));
        close(read0Rs , null , read0Conn);

        DataSource read1 = dataSource.getDataSourceWithName("read_1");
        Connection read1Conn = read1.getConnection();
        ResultSet read1Rs = RunScript.execute(read1Conn , new FileReader(initSqlPath));
        close(read1Rs , null , read1Conn);
    }

    @AfterEach
    protected void clearData() throws Exception {

        RoutingContext.clear();
        DataSource write0 = dataSource.getDataSourceWithName("write_0");
        Connection write0Conn = write0.getConnection();
        Statement write0Stmt = write0Conn.createStatement();
        write0Stmt.execute("SHUTDOWN");


        DataSource read0 = dataSource.getDataSourceWithName("read_0");
        Connection read0Conn = read0.getConnection();
        Statement read0Stmt = read0Conn.createStatement();
        read0Stmt.execute("SHUTDOWN");

        DataSource read1 = dataSource.getDataSourceWithName("read_1");
        Connection read1Conn = read1.getConnection();
        Statement read1Stmt = read1Conn.createStatement();
        read1Stmt.execute("SHUTDOWN");
    }

    private static void close(ResultSet rs , Statement stmt , Connection conn) throws Exception {
        if (Objects.nonNull(rs)) {
            rs.close();
        }
        if (Objects.nonNull(stmt)) {
            stmt.close();
        }
        if (Objects.nonNull(conn)) {
            conn.close();
        }
    }

}
