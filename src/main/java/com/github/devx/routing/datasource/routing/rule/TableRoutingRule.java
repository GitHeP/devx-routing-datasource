package com.github.devx.routing.datasource.routing.rule;

import com.github.devx.routing.datasource.routing.loadbalance.LoadBalancer;
import com.github.devx.routing.datasource.routing.loadbalance.RandomLoadBalancer;
import com.github.devx.routing.datasource.config.TableRuleConfiguration;
import com.github.devx.routing.datasource.sql.parser.SqlParser;
import com.github.devx.routing.datasource.sql.parser.SqlStatement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author he peng
 * @since 1.0
 */
public class TableRoutingRule extends AbstractRoutingRule {

    private final List<TableRuleConfiguration> tableRules;

    public TableRoutingRule(SqlParser sqlParser, LoadBalancer<String> loadBalancer, String writeDataSourceName, Set<String> readDataSourceNames, List<TableRuleConfiguration> tableRules) {
        super(sqlParser, loadBalancer, writeDataSourceName, readDataSourceNames);
        this.tableRules = tableRules;
    }

    @Override
    public String routing(SqlStatement statement) {

        if (Objects.isNull(statement.getTables())) {
            return null;
        }

        TableRuleConfiguration rule = null;
        for (TableRuleConfiguration tableRule : tableRules) {
            if (containsAny(tableRule.getTables() , statement.getTables())) {
                rule = tableRule;
                break;
            }
        }

        if (Objects.isNull(rule)) {
            return null;
        }

        String choose = null;
        if (rule.isForceWriteDataSource()) {
            choose = writeDataSourceName;
        } else if (statement.isWrite()) {
            return Objects.nonNull(rule.getWriteDataSource()) ? rule.getWriteDataSource() : writeDataSourceName;
        } else if (statement.isRead()) {
            if (Objects.nonNull(rule.getReadDataSources()) && rule.getReadDataSources().size() > 1) {
                LoadBalancer<String> balancer = new RandomLoadBalancer(new ArrayList<>(rule.getReadDataSources()));
                choose = balancer.choose();
            }
        }
        return choose;
    }

    @Override
    public int priority() {
        return 10;
    }

    private boolean containsAny(final Collection<?> coll1, final Collection<?> coll2) {
        if (coll1.size() < coll2.size()) {
            for (final Object aColl1 : coll1) {
                if (coll2.contains(aColl1)) {
                    return true;
                }
            }
        } else {
            for (final Object aColl2 : coll2) {
                if (coll1.contains(aColl2)) {
                    return true;
                }
            }
        }
        return false;
    }
}
