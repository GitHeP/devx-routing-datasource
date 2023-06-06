package com.lp.sql.routing.config;

import com.lp.sql.routing.RoutingTargetAttribute;
import com.lp.sql.routing.RoutingTargetType;
import com.lp.sql.routing.datasource.DataSourceAttribute;
import lombok.Data;

import java.util.Map;

/**
 * @author Peng He
 * @since 1.0
 */

@Data
public class DataSourceConfiguration {

    private String name;

    private RoutingTargetType type;

    private String dataSourceClass;

    /**
     * Native data source property configuration
     */
    private Map<String , Object> properties;

    /**
     * load balancing weight
     */
    private Integer weight;

    public RoutingTargetAttribute getRoutingTargetAttribute() {
        return new DataSourceAttribute(type, name, weight);
    }
}
