package com.lp.sql.routing.integration.mybatis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Peng He
 * @since 1.0
 */

@Configuration
@MapperScan(basePackages = {"com.lp.sql.routing.integration.mybatis"})
public class MybatisConfiguration {
}
