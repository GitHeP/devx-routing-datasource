package com.github.devx.routing.rule.group;

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


    @Override
    public String routing(RoutingKey key) {

        String targetName = null;
        for (RoutingGroup routingGroup : routingGroups) {

            if (log.isDebugEnabled()) {
                log.debug(routingGroup.toString());
            }

            targetName = routingGroup.routing(key);
            if (Objects.nonNull(targetName) && targetName.length() != 0) {
                break;
            }
        }
        RoutingContext.setRoutedDataSourceName(targetName);
        return targetName;
    }

    public synchronized void installFirst(RoutingGroup rule) {
        if (rule != null) {
            routingGroups.addFirst(rule);
        }
    }

    public synchronized void installFirst(List<RoutingGroup> rules) {
        if (rules != null && !rules.isEmpty()) {
            routingGroups.addAll(0 , rules);
        }
    }

    public synchronized void installLast(RoutingGroup rule) {
        if (rule != null) {
            routingGroups.addLast(rule);
        }
    }

    public synchronized void installLast(List<RoutingGroup> rules) {
        if (rules != null && !rules.isEmpty()) {
            routingGroups.addAll(routingGroups.size() - 1 , rules);
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
}
