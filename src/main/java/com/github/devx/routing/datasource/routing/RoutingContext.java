package com.github.devx.routing.datasource.routing;


import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author he peng
 * @since 1.0
 */
public class RoutingContext {

    private static final String CURRENTLY_EXECUTING_SQL_KEY = "CURRENTLY_EXECUTING_SQL";

    private static final String IN_TX_KEY = "IN_TX";

    private static final String TX_READ_ONLY_KEY = "TX_READ_ONLY";

    private static final String FORCE_DATA_SOURCE_KEY = "FORCE_DATA_SOURCE";

    private static final String ROUTED_DATA_SOURCE_NAME_KEY = "ROUTED_DATA_SOURCE_NAME";


    private static final TransmittableThreadLocal<Map<Object, Object>> RESOURCES = new TransmittableThreadLocal<Map<Object, Object>>() {
        @Override
        protected Map<Object, Object> initialValue() {
            return new HashMap<>();
        }
    };


    private RoutingContext() {
        throw new IllegalStateException("Instantiating RoutingContext is not allowed");
    }

    public static void clear() {
        RESOURCES.remove();
    }

    public static void addResource(Object key , Object val) {
        Map<Object, Object> map = RESOURCES.get();
        if (Objects.isNull(map)) {
            map = new HashMap<>();
            RESOURCES.set(map);
        }
        map.put(key , val);
    }


    public static Object getResource(Object key) {
        return RESOURCES.get().get(key);
    }

    public static void addCurrentlyExecutingSql(String sql) {
        addResource(CURRENTLY_EXECUTING_SQL_KEY, sql);
    }

    public static String getCurrentlyExecutingSqlKey() {
        Object value = getResource(CURRENTLY_EXECUTING_SQL_KEY);
        return Objects.nonNull(value) ? value.toString() : null;
    }

    public static void setInTx() {
        addResource(IN_TX_KEY , true);
    }

    public static boolean inTx() {
        Object value = getResource(IN_TX_KEY);
        return Objects.nonNull(value) && (boolean) value;
    }

    public static void setTxReadOnly(boolean readOnly) {
        addResource(TX_READ_ONLY_KEY , readOnly);
    }

    public static boolean getTxReadOnly() {
        Object value = getResource(TX_READ_ONLY_KEY);
        return Objects.nonNull(value) && (boolean) value;
    }

    public static void forceWrite() {
        addResource(FORCE_DATA_SOURCE_KEY , true);
    }

    public static boolean getForceWriteDataSource() {
        Object value = getResource(FORCE_DATA_SOURCE_KEY);
        return Objects.nonNull(value) && (boolean) value;
    }

    public static void setRoutedDataSourceName(String name) {
        addResource(ROUTED_DATA_SOURCE_NAME_KEY , name);
    }

    public static String getRoutedDataSourceName() {
        Object value = getResource(ROUTED_DATA_SOURCE_NAME_KEY);
        return Objects.nonNull(value) ? value.toString() : "unknown";
    }

}
