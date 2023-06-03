package com.github.devx.routing.integration.springboot;

import com.github.devx.routing.datasource.RoutingContext;
import com.github.devx.routing.integration.mybatis.AreaMapper;
import com.github.devx.routing.integration.mybatis.DepartmentMapper;
import com.github.devx.routing.integration.mybatis.EmployeeMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * testing Transactional
 *
 * @author Peng He
 * @since 1.0
 */

@Slf4j
class TransactionalTest extends BeforeAfterEachHandleDataTest {

    @Autowired
    EmployeeMapper employeeMapper;

    @Autowired
    DepartmentMapper departmentMapper;

    @Autowired
    AreaMapper areaMapper;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Transactional(rollbackFor = Throwable.class)
    @Commit
    @DirtiesContext
    @Test
    void testReadWriteTxFunc() {

        log.info("testing read write Tx");
        assertThat(RoutingContext.inTx()).isEqualTo(true);

        Map<String, Object> area = new HashMap<>();
        area.put("id" , 6);
        area.put("name" , "Paris");
        log.info("insert area {}" , area);
        int areaRow = areaMapper.insert(area);
        assertThat(areaRow).isEqualTo(1);

        Map<String, Object> dept = new HashMap<>();
        dept.put("id" , 6);
        dept.put("name" , "After-sales Service");
        dept.put("areaId" , 6);
        log.info("insert department {}" , dept);
        int deptRow = departmentMapper.insert(dept);
        assertThat(deptRow).isEqualTo(1);

        Map<String, Object> employee = new HashMap<>();
        employee.put("id" , 6);
        employee.put("name" , "Peng He");
        employee.put("departmentId" , 6);
        log.info("insert employee {}" , dept);
        int employeeRow = employeeMapper.insert(employee);
        assertThat(employeeRow).isEqualTo(1);

        assertThat(RoutingContext.getRoutedDataSourceName()).isEqualTo("write_0");

        String sql = "SELECT a.name AS area_name, d.name AS department_name, e.name AS employee_name\n" +
                "FROM area a\n" +
                "JOIN department d ON a.id = d.area_id\n" +
                "JOIN employee e ON d.id = e.department_id\n" +
                "WHERE a.id = ?";

        List<Map<String, Object>> results = jdbcTemplate.query(sql, new Object[]{6}, new ColumnMapRowMapper());
        assertThat(results)
                .extracting("AREA_NAME", "DEPARTMENT_NAME" , "EMPLOYEE_NAME")
                .containsExactlyInAnyOrder(
                        tuple("Paris", "After-sales Service", "Peng He")
                );

        assertThat(RoutingContext.getRoutedDataSourceName()).isEqualTo("write_0");
    }

    @Transactional(rollbackFor = Throwable.class , readOnly = true)
    @Commit
    @DirtiesContext
    @Test
    void testReadOnlyTxFunc() {

        log.info("testing read only Tx");
        assertThat(RoutingContext.inTx()).isEqualTo(true);
        assertThat(RoutingContext.getTxReadOnly()).isEqualTo(true);

        Map<String, Object> area = areaMapper.selectById(1L);
        assertThat(area).isNotNull()
                .extracting("ID" , "NAME")
                .containsExactlyInAnyOrder(1L , "East");

        Map<String, Object> dept = departmentMapper.selectById(1L);
        assertThat(dept).isNotNull()
                .extracting("ID" , "NAME" , "AREA_ID")
                .containsExactlyInAnyOrder(1L , "Research and Development" , 1);

        Map<String, Object> employee = employeeMapper.selectById(1L);
        assertThat(employee).isNotNull()
                .extracting("ID" , "NAME" , "DEPARTMENT_ID")
                .containsExactlyInAnyOrder(1L , "John Doe" , 1);

        String sql = "SELECT a.name AS area_name, d.name AS department_name, e.name AS employee_name , e.id as employee_id \n" +
                "FROM area a\n" +
                "JOIN department d ON a.id = d.area_id\n" +
                "JOIN employee e ON d.id = e.department_id\n" +
                "WHERE a.id = ?";

        List<Map<String, Object>> results = jdbcTemplate.query(sql, new Object[]{1}, new ColumnMapRowMapper());
        assertThat(results)
                .extracting("AREA_NAME", "DEPARTMENT_NAME" , "EMPLOYEE_NAME" , "EMPLOYEE_ID")
                .containsExactlyInAnyOrder(
                        tuple("East", "Research and Development", "John Doe" , 1L),
                        tuple("East", "Research and Development", "Jane Doe" , 2L)
                );

        assertThat(RoutingContext.getRoutedDataSourceName()).containsAnyOf("read_0" , "read_1");
    }

    @DirtiesContext
    @Test
    void testReadWriteNoTxFunc() {
        log.info("testing read write No Tx");
        assertThat(RoutingContext.inTx()).isEqualTo(false);

        Map<String, Object> area = new HashMap<>();
        area.put("id" , 6);
        area.put("name" , "Paris");
        log.info("insert area {}" , area);
        int areaRow = areaMapper.insert(area);
        assertThat(areaRow).isEqualTo(1);

        Map<String, Object> dept = new HashMap<>();
        dept.put("id" , 6);
        dept.put("name" , "After-sales Service");
        dept.put("areaId" , 6);
        log.info("insert department {}" , dept);
        int deptRow = departmentMapper.insert(dept);
        assertThat(deptRow).isEqualTo(1);

        Map<String, Object> employee = new HashMap<>();
        employee.put("id" , 6);
        employee.put("name" , "Peng He");
        employee.put("departmentId" , 6);
        log.info("insert employee {}" , dept);
        int employeeRow = employeeMapper.insert(employee);
        assertThat(employeeRow).isEqualTo(1);

        String sql = "SELECT a.name AS area_name, d.name AS department_name, e.name AS employee_name\n" +
                "FROM area a\n" +
                "JOIN department d ON a.id = d.area_id\n" +
                "JOIN employee e ON d.id = e.department_id\n" +
                "WHERE a.id = ?";

        RoutingContext.forceWrite();
        List<Map<String, Object>> results = jdbcTemplate.query(sql, new Object[]{6}, new ColumnMapRowMapper());
        assertThat(results)
                .extracting("AREA_NAME", "DEPARTMENT_NAME" , "EMPLOYEE_NAME")
                .containsExactlyInAnyOrder(
                        tuple("Paris", "After-sales Service", "Peng He")
                );

    }

    @DirtiesContext
    @Test
    void testReadOnlyNoTxFunc() {
        log.info("testing read only No Tx");
        assertThat(RoutingContext.inTx()).isEqualTo(false);

        Map<String, Object> area = areaMapper.selectById(1L);
        assertThat(area).isNotNull()
                .extracting("ID" , "NAME")
                .containsExactlyInAnyOrder(1L , "East");

        Map<String, Object> dept = departmentMapper.selectById(1L);
        assertThat(dept).isNotNull()
                .extracting("ID" , "NAME" , "AREA_ID")
                .containsExactlyInAnyOrder(1L , "Research and Development" , 1);

        Map<String, Object> employee = employeeMapper.selectById(1L);
        assertThat(employee).isNotNull()
                .extracting("ID" , "NAME" , "DEPARTMENT_ID")
                .containsExactlyInAnyOrder(1L , "John Doe" , 1);

        String sql = "SELECT a.name AS area_name, d.name AS department_name, e.name AS employee_name , e.id as employee_id \n" +
                "FROM area a\n" +
                "JOIN department d ON a.id = d.area_id\n" +
                "JOIN employee e ON d.id = e.department_id\n" +
                "WHERE a.id = ?";

        List<Map<String, Object>> results = jdbcTemplate.query(sql, new Object[]{1}, new ColumnMapRowMapper());
        assertThat(results)
                .extracting("AREA_NAME", "DEPARTMENT_NAME" , "EMPLOYEE_NAME" , "EMPLOYEE_ID")
                .containsExactlyInAnyOrder(
                        tuple("East", "Research and Development", "John Doe" , 1L),
                        tuple("East", "Research and Development", "Jane Doe" , 2L)
                );
    }

    @Override
    protected void clearData() throws Exception {
        log.info("just nothing");
    }
}
