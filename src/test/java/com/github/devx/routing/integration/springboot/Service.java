package com.github.devx.routing.integration.springboot;

import com.github.devx.routing.integration.mybatis.AreaMapper;
import com.github.devx.routing.integration.mybatis.DepartmentMapper;
import com.github.devx.routing.integration.mybatis.EmployeeMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(rollbackFor = Throwable.class)
    public void readWriteTxFunc() {

    }

    @Transactional(readOnly = true , rollbackFor = Throwable.class)
    public void readOnlyTxFunc() {

    }

    public void readWriteNoTxFunc() {

    }

    public void readOnlyNoTxFunc() {

    }
}
