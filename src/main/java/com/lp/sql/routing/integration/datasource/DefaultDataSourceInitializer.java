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

package com.lp.sql.routing.integration.datasource;

import org.joor.Reflect;
import org.springframework.format.support.DefaultFormattingConversionService;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @author Peng He
 * @since 1.0
 */
public class DefaultDataSourceInitializer implements GenericDataSourceInitializer<DataSource> {

    private static final DefaultFormattingConversionService CONVERSION_SERVICE = new DefaultFormattingConversionService();

    @Override
    public boolean supports(String dataSourceClassName) {
        return GenericDataSourceInitializer.super.supports(dataSourceClassName);
    }

    @Override
    public DataSource initialize(String dataSourceClassName, Map<String, Object> properties) {

        DataSource dataSource = Reflect.onClass(dataSourceClassName.trim()).create().get();
        configDataSource(dataSource , properties);
        return dataSource;
    }

    private void configDataSource(DataSource dataSource , Map<String, Object> properties) {
        Reflect dataSourceReflect = Reflect.on(dataSource);
        for (Map.Entry<String, Reflect> entry : dataSourceReflect.fields().entrySet()) {
            String fieldName = entry.getKey();
            if (properties.containsKey(fieldName)) {
                Object value = CONVERSION_SERVICE.convert(properties.get(fieldName), dataSourceReflect.field(fieldName).type());
                dataSourceReflect.set(fieldName , value);
            }
        }
    }
}
