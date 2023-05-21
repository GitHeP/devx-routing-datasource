package com.github.devx.routing.datasource.config;

import lombok.Data;

import java.util.Set;

/**
 * @author he peng
 * @since 1.0
 */

@Data
public class TableRuleConfiguration {

    private Set<String> tables;

    private boolean forceWriteDataSource;

    private String writeDataSource;

    private Set<String> readDataSources;
}
