package com.lp.sql.routing.rule;

import com.lp.sql.routing.RoutingTargetAttribute;
import com.lp.sql.routing.RoutingTargetType;
import com.lp.sql.routing.loadbalance.LoadBalance;
import com.lp.sql.routing.sql.AnnotationSqlAttribute;
import com.lp.sql.routing.sql.SqlAttribute;
import com.lp.sql.routing.sql.parser.RoutingTypeSqlHintConverter;
import com.lp.sql.routing.sql.parser.SqlHint;
import com.lp.sql.routing.sql.parser.SqlHintConverter;
import com.lp.sql.routing.sql.parser.SqlParser;

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
        return 20;
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
