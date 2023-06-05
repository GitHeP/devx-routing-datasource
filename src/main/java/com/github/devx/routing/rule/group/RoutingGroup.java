package com.github.devx.routing.rule.group;

import com.github.devx.routing.rule.RoutingRule;

import java.util.List;

/**
 * @author Peng He
 * @since 1.0
 */
public interface RoutingGroup<T extends RoutingRule> extends RoutingRule {

    void install(T rule);

    void install(List<T> rules);

    boolean uninstall(Class<T> type);
}
