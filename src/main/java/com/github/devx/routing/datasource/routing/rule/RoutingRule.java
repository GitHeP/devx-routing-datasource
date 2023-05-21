package com.github.devx.routing.datasource.routing.rule;

import com.github.devx.routing.datasource.routing.RoutingKey;

/**
 * Routing rules are responsible for the specific routing selection process.
 *
 * @author he peng
 * @since 1.0
 */
public interface RoutingRule {

    String routing(RoutingKey key);
}
