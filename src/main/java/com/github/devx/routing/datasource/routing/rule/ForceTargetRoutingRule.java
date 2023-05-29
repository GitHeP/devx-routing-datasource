package com.github.devx.routing.datasource.routing.rule;

import com.github.devx.routing.datasource.routing.RoutingContext;
import com.github.devx.routing.datasource.routing.RoutingKey;
import com.github.devx.routing.datasource.routing.loadbalance.RandomLoadBalancer;
import com.github.devx.routing.datasource.sql.parser.SqlStatement;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

/**
 * Route to the specified data source forcefully,
 * and randomly select between multiple data sources
 * if more than one are specified.
 *
 * @author he peng
 * @since 1.0
 *
 * @see RoutingContext#force(String...) ()
 */
public class ForceTargetRoutingRule implements StatementRoutingRule {

    @Override
    public String routing(SqlStatement statement) {

        Set<String> dataSources = RoutingContext.getForceDataSources();
        if (Objects.isNull(dataSources) || dataSources.isEmpty()) {
            return null;
        }

        RandomLoadBalancer balancer = new RandomLoadBalancer(new ArrayList<>(dataSources));
        return balancer.choose();
    }

    @Override
    public int priority() {
        return Integer.MIN_VALUE;
    }

    @Override
    public String routing(RoutingKey key) {
        return null;
    }


}
