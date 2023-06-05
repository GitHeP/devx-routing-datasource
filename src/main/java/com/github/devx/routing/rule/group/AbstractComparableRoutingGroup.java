package com.github.devx.routing.rule.group;

import com.github.devx.routing.rule.RoutingRule;

import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.List;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author Peng He
 * @since 1.0
 */
public abstract class AbstractComparableRoutingGroup<T extends RoutingRule> implements RoutingGroup<T> {

    protected final NavigableSet<T> routingRules;

    protected AbstractComparableRoutingGroup(Comparator<T> comparator) {
        this.routingRules = new ConcurrentSkipListSet<>(comparator);
    }

    @Override
    public void install(T rule) {
        if (rule != null) {
            routingRules.add(rule);
        }
    }

    @Override
    public void install(List<T> rules) {
        if (rules != null && !rules.isEmpty()) {
            routingRules.addAll(rules);
        }
    }

    @Override
    public boolean uninstall(final Class<T> type) {
        if (type == null || type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
            return false;
        }
        return routingRules.removeIf(rule -> Objects.equals(type , rule.getClass()));
    }
}
