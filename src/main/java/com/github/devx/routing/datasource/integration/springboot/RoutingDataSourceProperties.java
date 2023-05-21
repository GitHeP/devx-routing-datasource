package com.github.devx.routing.datasource.integration.springboot;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.Set;

/**
 * @author he peng
 * @since 1.0
 */

@ConfigurationProperties(prefix = "routing")
@Data
public class RoutingDataSourceProperties {

    public static final String DATA_SOURCE_CLASS_NAME_KEY = "dataSourceClassName";

    private Map<String , Map<String , Object>> dataSources;

    private String writeDataSource;

    private Set<String> readDataSources;

    private String loadBalancer;

    private RoutingRuleProperties rules;

}
