package com.github.devx.routing.sql.parser;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 *
 * @author Peng He
 * @since 1.0
 */
public class DefaultAnnotationSqlHintParser extends AbstractAnnotationSqlHintParser {

    private final List<SqlHintVisitor> visitors;

    public DefaultAnnotationSqlHintParser(List<SqlHintVisitor> visitors) {
        this.visitors = visitors;
    }

    @Override
    protected SqlHint parseHint(Map<String , String> hints) {
        SqlHint sqlHint = new SqlHint();
        for (SqlHintVisitor visitor : visitors) {
            visitor.visit(hints , sqlHint);
        }
        return sqlHint;
    }
}
