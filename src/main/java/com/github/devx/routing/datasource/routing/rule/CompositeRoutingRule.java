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
 * The composite RoutingRule implementation includes multiple
 * RoutingRule instances internally, which will be executed
 * in order according to priority. During the process,
 * if any RoutingRule returns a non-null result, the execution
 * process will be terminated, and the subsequent RoutingRules
 * will not be executed.
 *
 * @author he peng
 * @since 1.0
 */
public class CompositeRoutingRule extends AbstractRoutingRule {

    private final List<StatementRoutingRule> routingRules;

    public CompositeRoutingRule(SqlParser sqlParser, LoadBalancer<String> loadBalancer, String writeDataSourceName, Set<String> readDataSourceNames, List<StatementRoutingRule> routingRules) {
        super(sqlParser, loadBalancer, writeDataSourceName, readDataSourceNames);
        List<StatementRoutingRule> rules = new ArrayList<>();
        if (Objects.nonNull(routingRules)) {
            rules.addAll(routingRules);
            rules.sort(Comparator.comparingInt(PriorityRoutingRule::priority));
        }
        this.routingRules = Collections.unmodifiableList(rules);
    }

    @Override
    public String routing(SqlStatement statement) {

        String datasourceName = null;
        for (StatementRoutingRule routingRule : routingRules) {
            datasourceName = routingRule.routing(statement);
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
