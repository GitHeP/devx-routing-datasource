package com.github.devx.routing.datasource.sql.parser;

/**
 * @author he peng
 * @since 1.0
 */
public interface SqlStatementBuilder<T> {

    SqlStatement build(T obj);
}
