package com.github.devx.routing.integration.springboot;

import com.github.devx.routing.integration.mybatis.AreaMapper;
import com.github.devx.routing.integration.mybatis.DepartmentMapper;
import com.github.devx.routing.integration.mybatis.EmployeeMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Peng He
 * @since 1.0
 */

@org.springframework.stereotype.Service
@Slf4j
@AllArgsConstructor
public class Service {

    private final EmployeeMapper employeeMapper;

    private final DepartmentMapper departmentMapper;

    private final AreaMapper areaMapper;

    private final JdbcTemplate jdbcTemplate;

    @Transactional(rollbackFor = Throwable.class)
    public void readWriteTxFunc() {

        Map<String, Object> area = new HashMap<>();
        area.put("id" , 6);
        area.put("name" , "Paris");
        log.info("insert area {}" , area);
        areaMapper.insert(area);

        Map<String, Object> dept = new HashMap<>();
        dept.put("id" , 6);
        dept.put("name" , "After-sales Service");
        dept.put("areaId" , 6);
        log.info("insert department {}" , dept);
        departmentMapper.insert(dept);

        Map<String, Object> employee = new HashMap<>();
        employee.put("id" , 6);
        employee.put("name" , "Peng He");
        employee.put("departmentId" , 6);
        log.info("insert employee {}" , dept);
        employeeMapper.insert(employee);


        String sql = "SELECT a.name AS area_name, d.name AS department_name, e.name AS employee_name\n" +
                "FROM area a\n" +
                "JOIN department d ON a.id = d.area_id\n" +
                "JOIN employee e ON d.id = e.department_id" +
                "WHERE a.id = ?";

        List<Map<String, Object>> results = jdbcTemplate.query(sql, new Object[]{6}, new ColumnMapRowMapper());
        for (Map<String, Object> result : results) {
            log.info("query row {} ", result);
        }
    }

    @Transactional(readOnly = true , rollbackFor = Throwable.class)
    public void readOnlyTxFunc() {

    }

    public void readWriteNoTxFunc() {

    }

    public void readOnlyNoTxFunc() {

    }
}
