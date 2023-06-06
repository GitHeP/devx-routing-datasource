package com.lp.sql.routing.config;

import com.lp.sql.routing.sql.SqlType;
import lombok.Data;

import java.util.Set;

/**
 * @author Peng He
 * @since 1.0
 */

@Data
public class SqlTypeConfiguration {

    private Set<SqlType> sqlTypes;

    /**
     * Whether to allow all SQL types, true means allowed.
     * If it is false, routing to the specified data source
     * will be determined based on the sqlTypes configuration.
     */
    private Boolean allowAllSqlTypes;
}
