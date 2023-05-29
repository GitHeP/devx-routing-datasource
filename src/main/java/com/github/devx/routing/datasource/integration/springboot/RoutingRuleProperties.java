package com.github.devx.routing.datasource.integration.springboot;

import com.github.devx.routing.datasource.config.TableRuleConfiguration;
import lombok.Data;

/**
 * @author he peng
 * @since 1.0
 */

@Data
public class RoutingRuleProperties {

    private TableRuleConfiguration tableRule;
}
