package com.github.devx.routing.sql.parser;

/**
 * Convert SQL hint key-value configuration to the type specified by generics.
 * @author Peng He
 * @since 1.0
 */
public interface SqlHintConverter<T> {

    /**
     * extract and convert SQL hint
     * @param hint {@link SqlHint}
     * @return generics type T instance , maybe null
     */
    T convert(SqlHint hint);
}
