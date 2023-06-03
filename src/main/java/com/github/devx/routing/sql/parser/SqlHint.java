package com.github.devx.routing.sql.parser;

import com.github.devx.routing.RoutingTargetType;
import lombok.Data;

/**
 *
 * @author Peng He
 * @since 1.0
 */

@Data
public class SqlHint {

    private String nativeSql;

    private RoutingTargetType routingTargetType;
}
