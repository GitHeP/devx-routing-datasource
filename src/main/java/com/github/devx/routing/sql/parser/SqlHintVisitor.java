package com.github.devx.routing.sql.parser;

import java.util.Map;

/**
 * @author Peng He
 * @since 1.0
 */
public interface SqlHintVisitor {

    void visit(Map<String , String> hints , SqlHint hint);
}
