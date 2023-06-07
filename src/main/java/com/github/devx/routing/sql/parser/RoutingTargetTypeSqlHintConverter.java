package com.github.devx.routing.sql.parser;

import com.github.devx.routing.RoutingTargetType;

/**
 * @author Peng He
 * @since 1.0
 */
public class RoutingTargetTypeSqlHintConverter implements SqlHintConverter<RoutingTargetType> {

    protected static final String ROUTING_TYPE_KEY = "routingType";

    @Override
    public RoutingTargetType convert(SqlHint hint) {

        if (hint == null || hint.getHints() == null || hint.getHints().isEmpty() || !hint.getHints().containsKey(ROUTING_TYPE_KEY)) {
            return null;
        }

        return RoutingTargetType.valueOf(hint.getHints().get(ROUTING_TYPE_KEY).toUpperCase());
    }
}
