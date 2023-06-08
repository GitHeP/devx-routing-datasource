package com.github.devx.routing.config;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author Peng He
 * @since 1.0
 */

@Data
public class RoutingRuleConfiguration {

    private List<DatabaseRoutingConfiguration> databases;

    /**
     * key is table name
     * value Map key is data source name
     */
    private Map<String , Map<String , SqlTypeConfiguration>> tables;
}
