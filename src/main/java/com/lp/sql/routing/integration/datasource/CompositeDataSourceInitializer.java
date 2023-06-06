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

import com.lp.sql.routing.exception.ConfigurationException;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Peng He
 * @since 1.0
 */
public class CompositeDataSourceInitializer implements DataSourceInitializer {

    private final List<GenericDataSourceInitializer<?>> initializers = new ArrayList<>();

    public CompositeDataSourceInitializer(Set<GenericDataSourceInitializer<?>> initializers) {
        if (Objects.nonNull(initializers)) {
            this.initializers.addAll(initializers);
        }
        this.initializers.add(new DefaultDataSourceInitializer());
    }

    @Override
    public DataSource initialize(String dataSourceClassName, Map<String, Object> properties) {

        DataSource dataSource = null;
        for (GenericDataSourceInitializer<?> initializer : initializers) {
            if (initializer.supports(dataSourceClassName)) {
                dataSource = initializer.initialize(dataSourceClassName , properties);
                break;
            }
        }

        if (Objects.isNull(dataSource)) {
            throw new ConfigurationException(String.format("No such DataSourceInitializer found that supports [%s] DataSource" , dataSourceClassName));
        }

        return dataSource;
    }
}
