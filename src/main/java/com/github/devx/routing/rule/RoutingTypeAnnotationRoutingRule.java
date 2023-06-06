package com.github.devx.routing.rule;

import com.github.devx.routing.RoutingTargetAttribute;
import com.github.devx.routing.RoutingTargetType;
import com.github.devx.routing.loadbalance.LoadBalance;
import com.github.devx.routing.sql.AnnotationSqlAttribute;
import com.github.devx.routing.sql.SqlAttribute;
import com.github.devx.routing.sql.parser.RoutingTypeSqlHintConverter;
import com.github.devx.routing.sql.parser.SqlHint;
import com.github.devx.routing.sql.parser.SqlHintConverter;
import com.github.devx.routing.sql.parser.SqlParser;

/**
 * Annotation Hint routingType Routing Rule
 *
 * @author Peng He
 * @since 1.0
 */
public class RoutingTypeAnnotationRoutingRule extends AbstractRoutingRule {

    private static final SqlHintConverter<RoutingTargetType> SQL_HINT_CONVERTER = new RoutingTypeSqlHintConverter();

    public RoutingTypeAnnotationRoutingRule(SqlParser sqlParser, LoadBalance<RoutingTargetAttribute> readLoadBalance, LoadBalance<RoutingTargetAttribute> writeLoadBalance) {
        super(sqlParser, readLoadBalance, writeLoadBalance);
    }

    @Override
    public int priority() {
        return 10;
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

        RoutingTargetType rtt = SQL_HINT_CONVERTER.convert(sqlHint);
        if (rtt == null) {
            return null;
        }

        String choose = null;
        if (rtt.isWrite()) {
            choose = chooseWriteTargetName();
        } else if (rtt.isRead()) {
            choose = chooseReadTargetName();
        }

        return choose;
    }
}
