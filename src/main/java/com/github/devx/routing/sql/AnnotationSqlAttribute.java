package com.github.devx.routing.sql;

import com.github.devx.routing.sql.parser.SqlHint;

import java.util.Set;

/**
 * @author Peng He
 * @since 1.0
 */

public class AnnotationSqlAttribute implements SqlAttribute {

    private final SqlAttribute delegate;

    private final SqlHint sqlHint;

    public AnnotationSqlAttribute(SqlAttribute delegate , SqlHint sqlHint) {
        this.delegate = delegate;
        this.sqlHint = sqlHint;
    }

    public SqlHint getSqlHint() {
        return sqlHint;
    }

    @Override
    public String getSql() {
        return delegate.getSql();
    }

    @Override
    public SqlType getSqlType() {
        return delegate.getSqlType();
    }

    @Override
    public Object getStatement() {
        return delegate.getStatement();
    }

    @Override
    public boolean isWrite() {
        return delegate.isWrite();
    }

    @Override
    public boolean isRead() {
        return delegate.isRead();
    }

    @Override
    public Set<String> getDatabases() {
        return delegate.getDatabases();
    }

    @Override
    public Set<String> getTables() {
        return delegate.getTables();
    }

    @Override
    public Set<String> getNormalTables() {
        return delegate.getNormalTables();
    }

    @Override
    public Set<String> getJoinTables() {
        return delegate.getJoinTables();
    }

    @Override
    public Set<String> getSubTables() {
        return delegate.getSubTables();
    }
}
