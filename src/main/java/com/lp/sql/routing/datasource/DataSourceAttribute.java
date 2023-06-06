package com.lp.sql.routing.datasource;

import com.lp.sql.routing.RoutingTargetAttribute;
import com.lp.sql.routing.RoutingTargetType;

/**
 * @author Peng He
 * @since 1.0
 */

public class DataSourceAttribute implements RoutingTargetAttribute {

    private final RoutingTargetType routingTargetType;

    private final String name;

    private final Integer weight;

    public DataSourceAttribute(RoutingTargetType routingTargetType, String name, Integer weight) {
        this.routingTargetType = routingTargetType;
        this.name = name;
        if (weight == null) {
            weight = 0;
        }
        this.weight = weight;
    }

    @Override
    public RoutingTargetType getRoutingTargetType() {
        return routingTargetType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Integer getWeight() {
        return weight;
    }
}
