package com.github.devx.routing.config;

import lombok.Data;

import java.util.Map;

/**
 * @author Peng He
 * @since 1.0
 */

@Data
public class DatabaseRoutingConfiguration {

    private String name;

    private Map<String , SqlTypeConfiguration> nodes;
}
