package com.lp.sql.routing.config;

import lombok.Data;

import java.util.Map;

/**
 * @author Peng He
 * @since 1.0
 */

@Data
public class RoutingRuleConfiguration {

    /**
     * key is table name
     * value Map key is datasource name
     */
    private Map<String , Map<String , SqlTypeConfiguration>> tables;
}
