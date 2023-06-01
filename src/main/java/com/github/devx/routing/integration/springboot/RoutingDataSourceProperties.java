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

package com.github.devx.routing.integration.springboot;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.Set;

/**
 * @author he peng
 * @since 1.0
 */

@ConfigurationProperties(prefix = "routing")
@Data
public class RoutingDataSourceProperties {

    public static final String DATA_SOURCE_CLASS_NAME_KEY = "dataSourceClass";

    private Map<String , Map<String , Object>> dataSources;

    private String writeDataSource;

    private Set<String> readDataSources;

    private String loadBalancer;

    private RoutingRuleProperties rules;

}
