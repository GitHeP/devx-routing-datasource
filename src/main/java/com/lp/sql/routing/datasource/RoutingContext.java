/*
 *    Copyright 2023 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.lp.sql.routing.datasource;


import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Peng He
 * @since 1.0
 */
public class RoutingContext {

    private static final String CURRENTLY_EXECUTING_SQL_KEY = "CURRENTLY_EXECUTING_SQL";

    private static final String IN_TX_KEY = "IN_TX";

    private static final String TX_READ_ONLY_KEY = "TX_READ_ONLY";

    private static final String FORCE_WRITE_DATA_SOURCE_KEY = "FORCE_WRITE_DATA_SOURCE";

    private static final String FORCE_READ_DATA_SOURCE_KEY = "FORCE_READ_DATA_SOURCE";

    private static final String FORCE_DATA_SOURCE_KEY = "FORCE_DATA_SOURCE";

    private static final String ROUTED_DATA_SOURCE_NAME_KEY = "ROUTED_DATA_SOURCE_NAME";


    private static final TransmittableThreadLocal<Map<Object, Object>> RESOURCES = new TransmittableThreadLocal<Map<Object, Object>>() {
        @Override
        protected Map<Object, Object> initialValue() {
            return new HashMap<>(2 << 3);
        }
    };


    private RoutingContext() {
        throw new IllegalStateException("Instantiating RoutingContext is not allowed");
    }

    public static void clear() {
        RESOURCES.remove();
    }

    public static void addResource(Object key , Object val) {
        if (key == null) {
            throw new IllegalArgumentException("Invalid key parameter cannot be null");
        }
        if (val == null) {
            throw new IllegalArgumentException("Invalid Value parameter cannot be null");
        }
        Map<Object, Object> map = RESOURCES.get();
        if (Objects.isNull(map)) {
            map = new HashMap<>(2 << 3);
            RESOURCES.set(map);
        }
        map.put(key , val);
    }

    public static void removeResource(Object key) {
        Map<Object, Object> map = RESOURCES.get();
        if (Objects.nonNull(map)) {
            map.remove(key);
        }
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

    /**
     * forceWrite, forceRead, and force are mutually exclusive,
     * setting one will clear the others.
     *
     * @see #forceRead()
     * @see #force(String...)
     */
    public static void forceWrite() {
        addResource(FORCE_WRITE_DATA_SOURCE_KEY, true);
        removeResource(FORCE_DATA_SOURCE_KEY);
        removeResource(FORCE_READ_DATA_SOURCE_KEY);
    }

    public static boolean isForceWriteDataSource() {
        Object value = getResource(FORCE_WRITE_DATA_SOURCE_KEY);
        return Objects.nonNull(value) && (boolean) value;
    }

    /**
     * forceWrite, forceRead, and force are mutually exclusive,
     * setting one will clear the others.
     *
     * @see #forceWrite()
     * @see #force(String...)
     */
    public static void forceRead() {
        addResource(FORCE_READ_DATA_SOURCE_KEY, true);
        removeResource(FORCE_DATA_SOURCE_KEY);
        removeResource(FORCE_WRITE_DATA_SOURCE_KEY);
    }

    public static boolean isForceReadDataSource() {
        Object value = getResource(FORCE_READ_DATA_SOURCE_KEY);
        return Objects.nonNull(value) && (boolean) value;
    }

    /**
     * forceWrite, forceRead, and force are mutually exclusive,
     * setting one will clear the others.
     *
     * @see #forceWrite()
     * @see #forceRead()
     */
    public static void force(String ... dataSourceNames) {
        if (Objects.isNull(dataSourceNames) || dataSourceNames.length == 0) {
            return;
        }
        Set<String> dataSources = new HashSet<>(Arrays.asList(dataSourceNames));
        addResource(FORCE_DATA_SOURCE_KEY, dataSources);
        removeResource(FORCE_READ_DATA_SOURCE_KEY);
        removeResource(FORCE_WRITE_DATA_SOURCE_KEY);
    }

    @SuppressWarnings({"unchecked"})
    public static Set<String> getForceDataSources() {
        Object value = getResource(FORCE_DATA_SOURCE_KEY);
        return Objects.nonNull(value) ? (Set<String>) value : null;
    }

    public static void setRoutedDataSourceName(String name) {
        addResource(ROUTED_DATA_SOURCE_NAME_KEY , name);
    }

    public static String getRoutedDataSourceName() {
        Object value = getResource(ROUTED_DATA_SOURCE_NAME_KEY);
        return Objects.nonNull(value) ? value.toString() : "unknown";
    }

}
