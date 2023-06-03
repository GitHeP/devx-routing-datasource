package com.github.devx.routing.sql.parser;

import com.github.devx.routing.RoutingTargetType;

import java.util.Map;

/**
 * routingType Annotation sql hint Visitor
 *
 * @author Peng He
 * @since 1.0
 */
public class RoutingTargetTypeVisitor implements SqlHintVisitor {

    protected static final String ROUTING_TYPE_KEY = "routingType";


    @Override
    public void visit(Map<String , String> hints, SqlHint hint) {

        if (hints == null || hints.isEmpty() || !hints.containsKey(ROUTING_TYPE_KEY) || hint == null) {
            return;
        }
        RoutingTargetType rtt = RoutingTargetType.valueOf(hints.get(ROUTING_TYPE_KEY).toUpperCase());
        hint.setRoutingTargetType(rtt);
    }
}
