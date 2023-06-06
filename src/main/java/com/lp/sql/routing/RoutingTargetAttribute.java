package com.lp.sql.routing;

/**
 * @author Peng He
 * @since 1.0
 */
public interface RoutingTargetAttribute {

    RoutingTargetType getRoutingTargetType();

    String getName();

    Integer getWeight();
}
