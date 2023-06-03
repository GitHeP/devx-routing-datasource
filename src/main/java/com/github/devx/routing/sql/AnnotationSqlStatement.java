package com.github.devx.routing.sql;

import com.github.devx.routing.RoutingTargetType;
import com.github.devx.routing.sql.parser.SqlHint;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Peng He
 * @since 1.0
 */

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class AnnotationSqlStatement extends SqlStatement {

    private RoutingTargetType routingTargetType;

    public static AnnotationSqlStatement of(SqlStatement statement , SqlHint sqlHint) {
        AnnotationSqlStatement ast = new AnnotationSqlStatement();
        // TODO value copy
        return ast;
    }
}
