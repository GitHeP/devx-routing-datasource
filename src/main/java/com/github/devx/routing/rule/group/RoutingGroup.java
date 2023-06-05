package com.github.devx.routing.rule.group;

import com.github.devx.routing.rule.RoutingRule;

import java.util.List;

/**
 * @author Peng He
 * @since 1.0
 */
public interface RoutingGroup extends RoutingRule {

    void registerRoutingRule(RoutingRule rule);

    void registerRoutingRule(List<RoutingRule> rules);

}
