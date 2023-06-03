package com.github.devx.routing.sql.parser;

import com.github.devx.routing.exception.AnnotationSqlException;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Peng He
 * @since 1.0
 */
public abstract class AbstractAnnotationSqlHintParser implements AnnotationSqlHintParser {

    protected static final Pattern ANNOTATION_PATTERN = Pattern.compile("/\\*!(.*?)\\*/");

    @Override
    public SqlHint parse(String sql) {

        if (sql == null || sql.length() == 0) {
            return null;
        }

        String hint = null;
        Matcher matcher = ANNOTATION_PATTERN.matcher(sql);
        if (matcher.find()) {
            hint = matcher.group(1);
        }
        SqlHint sqlHint = parseHint(splitHint(hint));
        if (sqlHint != null) {
            sqlHint.setNativeSql(matcher.replaceAll("").trim());
        }
        return sqlHint;
    }

    private Map<String , String> splitHint(String hintString) {
        if (hintString == null || hintString.length() == 0) {
            return null;
        }

        String[] hints = hintString.split(";");
        if (hints.length == 0) {
            return null;
        }

        Map<String , String> hintMap = new HashMap<>();
        for (String hint : hints) {
            String[] kv = hint.split("=");
            if (kv.length != 2) {
                throw new AnnotationSqlException("hint key and value in SQL annotations must be separated by an equal sign (=)");
            }
            hintMap.put(kv[0] , kv[1]);
        }

        return hintMap;
    }

    protected abstract SqlHint parseHint(Map<String , String> hints);
}
