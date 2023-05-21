package com.github.devx.routing.datasource.sql.parser;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * @author he peng
 * @since 1.0
 */

@Data
@Accessors(chain = true)
public class SqlStatement {

    private String sql;

    private Object statement;

    private boolean write;

    private boolean read;

    private Set<String> databases;

    private Set<String> tables;

    private String fromTable;

    private Set<String> joinTables;

    private Set<String> subTables;

}
