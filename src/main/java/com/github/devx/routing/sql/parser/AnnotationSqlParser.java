package com.github.devx.routing.sql.parser;

import com.github.devx.routing.RoutingTargetType;
import com.github.devx.routing.sql.AnnotationSqlStatement;
import com.github.devx.routing.sql.SqlStatement;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public SqlStatement parse(String sql) {

        SqlStatement statement;
        SqlHint sqlHint = sqlHintParser.parse(sql);
        if (Objects.nonNull(sqlHint)) {
            SqlStatement nativeSqlStmt = delegate.parse(sqlHint.getNativeSql());
            statement = AnnotationSqlStatement.of(nativeSqlStmt , sqlHint);
        } else {
            statement = delegate.parse(sql);
        }
        return statement;
    }

    private RoutingTargetType parseRoutingTargetType(String hint) {
        if (hint == null || hint.length() != 0) {
            return null;
        }

        return null;
    }
}
