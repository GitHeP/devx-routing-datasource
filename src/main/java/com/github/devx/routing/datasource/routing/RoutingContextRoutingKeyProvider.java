package com.github.devx.routing.datasource.routing;

/**
 * Provide RoutingKey instance from routing context
 *
 * @author he peng
 * @since 1.0
 *
 * @see RoutingContext
 * @see RoutingKey
 */
public class RoutingContextRoutingKeyProvider implements RoutingKeyProvider {

    @Override
    public RoutingKey getRoutingKey() {
        String sql = RoutingContext.getCurrentlyExecutingSqlKey();
        RoutingKey routingKey = new RoutingKey().setSql(sql);
        if (RoutingContext.isForceWriteDataSource()) {
            routingKey.setForeWriteDataSource(true);
        }
        return routingKey;
    }
}
