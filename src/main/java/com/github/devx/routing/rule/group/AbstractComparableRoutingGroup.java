package com.github.devx.routing.rule.group;

import com.github.devx.routing.config.RoutingConfiguration;
import com.github.devx.routing.config.RoutingGroupPluggableConfiguration;
import com.github.devx.routing.rule.RoutingRule;

import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author Peng He
 * @since 1.0
 */
public abstract class AbstractComparableRoutingGroup<T extends RoutingRule> implements RoutingGroup<T> {

    protected final RoutingConfiguration routingConf;

    protected final NavigableSet<T> routingRules;

    protected AbstractComparableRoutingGroup(RoutingConfiguration routingConf, Comparator<T> comparator) {
        this.routingConf = routingConf;
        this.routingRules = new ConcurrentSkipListSet<>(comparator);
    }

    @Override
    public void install(T rule) {
        if (rule != null && isEnableRule(rule)) {
            routingRules.add(rule);
        }
    }

    @Override
    public void install(List<T> rules) {
        if (rules != null && !rules.isEmpty()) {
            install(rules);
        }
    }

    @Override
    public boolean uninstall(final Class<T> type) {
        if (type == null || type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
            return false;
        }
        return routingRules.removeIf(rule -> Objects.equals(type, rule.getClass()));
    }

    private boolean isEnableRule(T rule) {

        Optional<RoutingGroupPluggableConfiguration> optional = routingConf.getGroups()
                .stream()
                .filter(configuration -> (Objects.equals(configuration.getGroupName(), this.getClass().getName()) || Objects.equals(configuration.getGroupName(), this.getClass().getSimpleName())))
                .findAny();

        if (!optional.isPresent()) {
            return true;
        }

        Map<String, Boolean> rules = optional.get().getRules();
        if (rules == null || rules.isEmpty()) {
            return true;
        }

        Class<? extends RoutingRule> ruleClass = rule.getClass();
        for (Map.Entry<String, Boolean> entry : rules.entrySet()) {
            String ruleName = entry.getKey();
            boolean match = Objects.equals(ruleName, ruleClass.getSimpleName()) || Objects.equals(ruleName, ruleClass.getName());
            if (match) {
                Boolean enable = entry.getValue();
                return Objects.nonNull(enable) && Boolean.FALSE.equals(enable);
            }
        }
        return true;
    }
}
