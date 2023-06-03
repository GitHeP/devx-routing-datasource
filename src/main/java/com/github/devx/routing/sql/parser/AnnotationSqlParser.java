package com.github.devx.routing.sql.parser;

import com.github.devx.routing.sql.AnnotationSqlAttribute;
import com.github.devx.routing.sql.SqlAttribute;

import java.util.Objects;

/**
 * author he peng
 * date 2023/6/3 18:03
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
