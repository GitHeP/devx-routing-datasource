package com.github.devx.routing.datasource.routing;

import com.github.devx.routing.datasource.sql.parser.SqlStatement;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author he peng
 * @since 1.0
 */

@Data
@Accessors(chain = true)
public class RoutingKey {

    private String sql;

    private SqlStatement statement;

    /**
     * Whether to force routing to write datasource
     */
    private boolean foreWriteDataSource;
}
