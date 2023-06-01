package com.github.devx.routing.config;

import com.github.devx.routing.sql.SqlStatementType;
import lombok.Data;

import java.util.Set;

/**
 * @author Peng He
 * @since 1.0
 */

@Data
public class SqlTypeConfiguration {

    private Set<SqlStatementType> sqlTypes;

    /**
     * Whether to allow all SQL types, true means allowed.
     * If it is false, routing to the specified data source
     * will be determined based on the sqlTypes configuration.
     */
    private Boolean allowAllSqlTypes;
}
