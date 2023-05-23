package com.github.devx.routing.datasource.routing.rule;

import com.github.devx.routing.datasource.routing.RoutingKey;

/**
 * Routing rules are responsible for the specific routing selection process.
 *
 * @author he peng
 * @since 1.0
 */
public interface RoutingRule {

    /**
     * routing with RoutingKey
     * @param key {@link RoutingKey}
     * @return datasource name
     */
    String routing(RoutingKey key);
}
