package com.github.devx.routing.rule;

import com.github.devx.routing.sql.AnnotationSqlAttribute;
import com.github.devx.routing.sql.SqlAttribute;
import com.github.devx.routing.sql.parser.RoutingTargetNameSqlHintConverter;
import com.github.devx.routing.sql.parser.SqlHint;
import com.github.devx.routing.sql.parser.SqlHintConverter;

/**
 * Annotation Hint routingTargetName Routing Rule
 *
 * @author Peng He
 * @since 1.0
 */
public class RoutingNameSqlHintRoutingRule implements SqlAttributeRoutingRule {

    private static final SqlHintConverter<String> SQL_HINT_CONVERTER = new RoutingTargetNameSqlHintConverter();

    @Override
    public int priority() {
        return 9;
    }

    @Override
    public String routing(RoutingKey key) {
        return null;
    }

    @Override
    public String routing(SqlAttribute attribute) {

        if (!(attribute instanceof AnnotationSqlAttribute)) {
            return null;
        }

        SqlHint sqlHint = ((AnnotationSqlAttribute) attribute).getSqlHint();
        if (sqlHint == null) {
            return null;
        }

        return SQL_HINT_CONVERTER.convert(sqlHint);
    }
}
