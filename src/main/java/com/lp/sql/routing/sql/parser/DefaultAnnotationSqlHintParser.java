package com.lp.sql.routing.sql.parser;

import com.lp.sql.routing.exception.AnnotationSqlException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Peng He
 * @since 1.0
 */
public class DefaultAnnotationSqlHintParser implements AnnotationSqlHintParser {

    private static final Pattern ANNOTATION_PATTERN = Pattern.compile("/\\*!(.*?)\\*/");

    private static final String HINT_DELIMITER_REGEX = ";";

    private static final String HINT_KEY_VALUE_DELIMITER = "=";

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
        String nativeSql = matcher.replaceAll("").trim();
        return new SqlHint(splitHint(hint) , nativeSql);
    }

    private Map<String , String> splitHint(String hintString) {
        if (hintString == null || hintString.length() == 0) {
            return Collections.emptyMap();
        }

        String[] hints = hintString.split(HINT_DELIMITER_REGEX);
        if (hints.length == 0) {
            return Collections.emptyMap();
        }

        Map<String , String> hintMap = new HashMap<>();
        for (String hint : hints) {
            String[] kv = hint.split(HINT_KEY_VALUE_DELIMITER);
            if (kv.length != 2) {
                throw new AnnotationSqlException("hint key and value in SQL annotations must be separated by an equal sign (=)");
            }
            hintMap.put(kv[0].trim() , kv[1].trim());
        }

        return hintMap;
    }
}
