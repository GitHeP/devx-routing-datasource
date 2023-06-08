package com.github.devx.routing.rule.group;

import com.github.devx.routing.config.RoutingConfiguration;
import com.github.devx.routing.datasource.RoutingContext;
import com.github.devx.routing.exception.InternalRuntimeException;
import com.github.devx.routing.rule.RoutingKey;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author Peng He
 * @since 1.0
 */

@Slf4j
public class CompositeRoutingGroup implements RoutingGroup<RoutingGroup> {

    private final LinkedList<RoutingGroup> routingGroups = new LinkedList<RoutingGroup>();

    private final RoutingConfiguration routingConfiguration;

    public CompositeRoutingGroup(RoutingConfiguration routingConfiguration) {
        this.routingConfiguration = routingConfiguration;
    }

    @Override
    public String routing(RoutingKey key) {

        String targetName = null;
        for (RoutingGroup routingGroup : routingGroups) {

            if (log.isDebugEnabled()) {
                log.debug(routingGroup.toString());
            }

            targetName = routingGroup.routing(key);
            if (Objects.nonNull(targetName) && targetName.length() != 0) {
                if (log.isDebugEnabled()) {
                    String msg = new StringBuilder("Hit Routing Group")
                            .append(System.lineSeparator())
                            .append("Group: ").append(routingGroup.getClass().getSimpleName())
                            .append(System.lineSeparator())
                            .append("TargetName: ").append(targetName)
                            .append(System.lineSeparator())
                            .append("SQL: ").append(key.getSql())
                            .toString();
                    log.debug(msg);
                }
                break;
            }
        }
        RoutingContext.setRoutedDataSourceName(targetName);
        return targetName;
    }

    public synchronized void installFirst(RoutingGroup rule) {
        if (rule != null && isEnableGroup(rule)) {
            routingGroups.addFirst(rule);
        }
    }

    public synchronized void installFirst(List<RoutingGroup> rules) {
        if (rules != null && !rules.isEmpty()) {
            routingGroups.addAll(0 , rules);
            for (RoutingGroup group : rules) {
                installFirst(group);
            }
        }
    }

    public synchronized void installLast(RoutingGroup rule) {
        if (rule != null && isEnableGroup(rule)) {
            routingGroups.addLast(rule);
        }
    }

    public synchronized void installLast(List<RoutingGroup> rules) {
        if (rules != null && !rules.isEmpty()) {
            for (RoutingGroup group : rules) {
                installLast(group);
            }
        }
    }

    @Override
    public void install(RoutingGroup rule) {
        throw new InternalRuntimeException("Unsupported method install");
    }

    @Override
    public void install(List<RoutingGroup> rules) {
        throw new InternalRuntimeException("Unsupported method install");
    }

    @Override
    public boolean uninstall(Class<RoutingGroup> type) {
        return routingGroups.removeIf(rule -> Objects.equals(type , rule.getClass()));
    }

    private boolean isEnableGroup(RoutingGroup group) {
        if (group instanceof EmbeddedRoutingGroup) {
            return true;
        }
        return routingConfiguration.getGroups()
                .stream()
                .anyMatch(configuration -> (Objects.equals(configuration.getGroupName(), group.getClass().getName()) || Objects.equals(configuration.getGroupName(), group.getClass().getSimpleName())) && Boolean.TRUE.equals(configuration.getEnable()));
    }
}
