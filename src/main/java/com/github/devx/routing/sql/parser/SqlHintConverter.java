package com.github.devx.routing.sql.parser;

/**
 * @author Peng He
 * @since 1.0
 */
public interface SqlHintConverter<T> {

    T convert(SqlHint hint);
}
