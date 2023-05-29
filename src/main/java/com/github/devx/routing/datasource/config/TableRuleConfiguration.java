package com.github.devx.routing.datasource.config;

import lombok.Data;

import java.util.Map;
import java.util.Set;

/**
 * @author he peng
 * @since 1.0
 */

@Data
public class TableRuleConfiguration {

    /**
     * key - table name
     * value - datasource names
     */
    private Map<String , Set<String>> tables;
}
