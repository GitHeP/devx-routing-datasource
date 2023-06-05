package com.github.devx.routing.sql.parser;

import lombok.Data;

import java.util.Map;

/**
 *
 * @author Peng He
 * @since 1.0
 */

@Data
public class SqlHint {

    /**
     * key is hint key , value is hint value
     */
    private Map<String , String> hints;

    private String nativeSql;

    public SqlHint() {
    }

    public SqlHint(Map<String, String> hints, String nativeSql) {
        this.hints = hints;
        this.nativeSql = nativeSql;
    }
}
