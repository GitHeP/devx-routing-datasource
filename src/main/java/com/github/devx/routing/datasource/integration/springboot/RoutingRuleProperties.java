package com.github.devx.routing.datasource.integration.springboot;

import com.github.devx.routing.datasource.config.TableRuleConfiguration;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author he peng
 * @since 1.0
 */

@Data
public class RoutingRuleProperties {

    private List<Map<String , TableRuleConfiguration>> tables;
}
