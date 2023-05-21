package com.github.devx.routing.datasource.sql.parser;

/**
 * SQL is an important basis for determining the target data source in the routing process of data sources.
 * The purpose of the SQL parser is to parse the SQL and obtain the judgment basis information for selecting
 * the final data source in the subsequent routing process.
 *
 * @author he peng
 * @since 1.0
 *
 * @see JSqlParser
 * @see SqlStatement
 */
public interface SqlParser {

    /**
     * Parse a given SQL statement
     * @param sql sql statement
     * @return {@link SqlStatement}
     */
    SqlStatement parse(String sql);
}
