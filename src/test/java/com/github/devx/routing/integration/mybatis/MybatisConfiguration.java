package com.github.devx.routing.integration.mybatis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author he peng
 * @since 1.0
 */

@Configuration
@MapperScan(basePackages = {"com.github.devx.routing.integration.mybatis"})
public class MybatisConfiguration {
}
