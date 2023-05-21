package com.github.devx.routing.datasource.routing;

/**
 * @author he peng
 * @since 1.0
 */
public interface RoutingContextClearable {

    default void forceClear() {
        RoutingContext.clear();
    }

    default void clearingWithoutTx() {
        if (!RoutingContext.inTx()) {
            RoutingContext.clear();
        }
    }

}
