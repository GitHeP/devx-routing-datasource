package com.github.devx.routing.config;

import lombok.Data;

import java.util.Map;

/**
 * @author Peng He
 * @since 1.0
 */

@Data
public class RoutingRuleConfiguration {

    /**
     * key is database name
     * value Map key is data source name
     */
    private Map<String , Map<String , SqlTypeConfiguration>> databases;

    /**
     * key is table name
     * value Map key is data source name
     */
    private Map<String , Map<String , SqlTypeConfiguration>> tables;
}
