package com.github.devx.routing.integration.springboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * testing using JdbcTemplate
 *
 * @author Peng He
 * @since 1.0
 */
class JdbcTemplateTest extends InitDataTest {

    @Autowired
    JdbcTemplate jdbcTemplate;
}
