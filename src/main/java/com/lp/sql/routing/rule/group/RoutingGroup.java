package com.lp.sql.routing.rule.group;

import com.lp.sql.routing.rule.RoutingRule;

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
