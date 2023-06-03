package com.github.devx.routing.rule;

import com.github.devx.routing.RoutingTargetType;
import com.github.devx.routing.loadbalance.LoadBalancer;
import com.github.devx.routing.sql.AnnotationSqlAttribute;
import com.github.devx.routing.sql.SqlAttribute;
import com.github.devx.routing.sql.parser.SqlHint;
import com.github.devx.routing.sql.parser.SqlParser;

import java.util.Set;

/**
 * Annotation Hint routingType Routing Rule
 *
 * @author Peng He
 * @since 1.0
 */
public class RoutingTypeAnnotationRoutingRule extends AbstractRoutingRule {


    protected RoutingTypeAnnotationRoutingRule(SqlParser sqlParser, LoadBalancer<String> loadBalancer, String writeDataSourceName, Set<String> readDataSourceNames) {
        super(sqlParser, loadBalancer, writeDataSourceName, readDataSourceNames);
    }

    @Override
    public int priority() {
        return Integer.MIN_VALUE + 100;
    }

    @Override
    public String routing(SqlAttribute attribute) {
        if (!(attribute instanceof AnnotationSqlAttribute)) {
            return null;
        }

        SqlHint sqlHint = ((AnnotationSqlAttribute) attribute).getSqlHint();
        if (sqlHint == null) {
            return null;
        }

        RoutingTargetType rtt = sqlHint.getRoutingTargetType();
        if (rtt == null) {
            return null;
        }

        String choose = null;
        if (rtt.isWrite()) {
            choose = writeDataSourceName;
        } else if (rtt.isRead()) {
            choose = loadBalancer.choose();
        }

        return choose;
    }
}
