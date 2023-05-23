package com.github.devx.routing.datasource.routing.rule;

import com.github.devx.routing.datasource.sql.parser.SqlStatement;

/**
 * Route selection based on SQL statements.
 *
 * @author he peng
 * @since 1.0
 */
public interface StatementRoutingRule {

    /**
     * routing with sql statement
     * @param statement {@link SqlStatement}
     * @return datasource name
     */
    String routing(SqlStatement statement);
}
