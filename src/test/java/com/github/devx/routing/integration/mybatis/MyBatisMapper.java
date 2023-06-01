package com.github.devx.routing.integration.mybatis;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * @author Peng He
 * @since 1.0
 */

@Mapper
public interface MyBatisMapper {

    Map<String , Object> selectEmployeeById(Long id);
}
