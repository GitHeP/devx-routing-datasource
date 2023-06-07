package com.github.devx.routing.sql.parser;

/**
 * @author Peng He
 * @since 1.0
 */
public class RoutingTargetNameSqlHintConverter implements SqlHintConverter<String> {

    protected static final String ROUTING_TARGET_NAME_KEY = "routingTargetName";


    @Override
    public String convert(SqlHint hint) {

        if (hint == null || hint.getHints() == null || hint.getHints().isEmpty() || !hint.getHints().containsKey(ROUTING_TARGET_NAME_KEY)) {
            return null;
        }
        return hint.getHints().get(ROUTING_TARGET_NAME_KEY);
    }
}
