package com.lp.sql.routing.sql;

import java.util.Set;

/**
 * @author Peng He
 * @since 1.0
 */
public interface SqlAttribute {

    String getSql();

    SqlType getSqlType();

    Object getStatement();

    boolean isWrite();

    boolean isRead();

    Set<String> getDatabases();

    Set<String> getTables();

    Set<String> getNormalTables();

    Set<String> getJoinTables();

    Set<String> getSubTables();
}
