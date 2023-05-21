package com.github.devx.routing.datasource.routing.rule;

import com.github.devx.routing.datasource.routing.loadbalance.LoadBalancer;
import com.github.devx.routing.datasource.sql.parser.SqlParser;
import com.github.devx.routing.datasource.sql.parser.SqlStatement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author he peng
 * @since 1.0
 */
public class CompositeRoutingRule extends AbstractRoutingRule {

    private final List<AbstractRoutingRule> routingRules;

    public CompositeRoutingRule(SqlParser sqlParser, LoadBalancer<String> loadBalancer, String writeDataSourceName, Set<String> readDataSourceNames, List<AbstractRoutingRule> routingRules) {
        super(sqlParser, loadBalancer, writeDataSourceName, readDataSourceNames);
        List<AbstractRoutingRule> rules = new ArrayList<>();
        if (Objects.nonNull(routingRules)) {
            rules.addAll(routingRules);
            rules.sort(Comparator.comparingInt(PriorityRoutingRule::priority));
        }
        this.routingRules = Collections.unmodifiableList(rules);
    }

    @Override
    protected String internalRouting(SqlStatement statement) {

        String datasourceName = null;
        for (AbstractRoutingRule routingRule : routingRules) {
            datasourceName = routingRule.internalRouting(statement);
            if (Objects.nonNull(datasourceName) && datasourceName.length() != 0) {
                break;
            }
        }
        return datasourceName;
    }

    @Override
    public int priority() {
        return Integer.MIN_VALUE;
    }
}
