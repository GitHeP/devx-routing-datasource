package com.lp.sql.routing.sql.parser;

import com.lp.sql.routing.sql.AnnotationSqlAttribute;
import com.lp.sql.routing.sql.SqlAttribute;

import java.util.Objects;

/**
 * @author Peng He
 * @since 1.0
 */
public class AnnotationSqlParser implements SqlParser {


    private final SqlParser delegate;

    private final AnnotationSqlHintParser sqlHintParser;


    public AnnotationSqlParser(SqlParser delegate, AnnotationSqlHintParser sqlHintParser) {
        this.delegate = delegate;
        this.sqlHintParser = sqlHintParser;
    }

    @Override
    public SqlAttribute parse(String sql) {

        SqlAttribute sqlAttribute;
        SqlHint sqlHint = sqlHintParser.parse(sql);
        if (Objects.nonNull(sqlHint)) {
            sqlAttribute = new AnnotationSqlAttribute(delegate.parse(sqlHint.getNativeSql()) , sqlHint);
        } else {
            sqlAttribute = delegate.parse(sql);
        }
        return sqlAttribute;
    }
}
