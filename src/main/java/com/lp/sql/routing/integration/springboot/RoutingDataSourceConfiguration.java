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

package com.lp.sql.routing.integration.springboot;

import com.lp.sql.routing.config.DataSourceConfiguration;
import com.lp.sql.routing.config.RoutingConfiguration;
import com.lp.sql.routing.datasource.DataSourceWrapper;
import com.lp.sql.routing.datasource.DefaultRoutingDataSource;
import com.lp.sql.routing.datasource.RoutingContextRoutingKeyProvider;
import com.lp.sql.routing.datasource.RoutingKeyProvider;
import com.lp.sql.routing.exception.ConfigurationException;
import com.lp.sql.routing.integration.datasource.CompositeDataSourceInitializer;
import com.lp.sql.routing.integration.datasource.DataSourceInitializer;
import com.lp.sql.routing.integration.datasource.GenericDataSourceInitializer;
import com.lp.sql.routing.rule.RoutingRule;
import com.lp.sql.routing.rule.group.CompositeRoutingGroup;
import com.lp.sql.routing.rule.group.EmbeddedRoutingGroup;
import com.lp.sql.routing.rule.group.RoutingGroup;
import com.lp.sql.routing.sql.parser.JSqlParser;
import com.lp.sql.routing.sql.parser.SqlParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Peng He
 * @since 1.0
 */

@EnableConfigurationProperties(RoutingProperties.class)
public class RoutingDataSourceConfiguration {

    @Bean
    public DataSourceInitializer compositeDataSourceInitializer(@Autowired(required = false) Set<GenericDataSourceInitializer<?>> initializers) {
        return new CompositeDataSourceInitializer(initializers);
    }

    @Bean
    public RoutingDataSourceTransactionManager routingDataSourceTransactionManager(DataSource dataSource) {
        return new RoutingDataSourceTransactionManager(dataSource);
    }

    @Bean
    @ConditionalOnMissingBean(RoutingKeyProvider.class)
    public RoutingKeyProvider routingKeyProvider() {
        return new RoutingContextRoutingKeyProvider();
    }

    @Bean
    @ConditionalOnMissingBean(SqlParser.class)
    public SqlParser sqlParser() {
        return new JSqlParser();
    }

    @Bean
    @SuppressWarnings({"all"})
    public RoutingGroup<? extends RoutingRule> compositeRoutingGroup(@Autowired(required = false) List<RoutingGroup> routingGroups , RoutingProperties routingProperties, SqlParser sqlParser) {
        CompositeRoutingGroup compositeRoutingGroup = new CompositeRoutingGroup();
        compositeRoutingGroup.installFirst(routingGroups);
        compositeRoutingGroup.installLast(new EmbeddedRoutingGroup(routingProperties.getRouting() , sqlParser));
        return compositeRoutingGroup;
    }

    @Bean
    public DataSource routingDataSource(RoutingKeyProvider routingKeyProvider, RoutingProperties properties , DataSourceInitializer dataSourceInitializer , @Autowired @Qualifier("compositeRoutingGroup") RoutingRule routingRule) {

        if (Objects.isNull(properties.getRouting().getMasters()) || properties.getRouting().getMasters().isEmpty()) {
            throw new ConfigurationException("Configuration item [masters] is required");
        }

        RoutingConfiguration routing = properties.getRouting();

        Map<String , DataSource> dataSources = new HashMap<>();
        for (DataSourceConfiguration dataSourceConf : routing.getDataSources()) {
            Object dataSourceClassName = dataSourceConf.getDataSourceClass();
            if (Objects.isNull(dataSourceClassName)) {
                throw new ConfigurationException(String.format("Configuration item [%s] is required" , RoutingConfiguration.DATA_SOURCE_CLASS_NAME_KEY));
            }
            DataSource dataSource = dataSourceInitializer.initialize(dataSourceClassName.toString() , dataSourceConf.getProps());
            dataSources.put(dataSourceConf.getName() , new DataSourceWrapper(dataSource , dataSourceConf.getRoutingTargetAttribute()));
        }


        return new DefaultRoutingDataSource(dataSources , routingRule , routingKeyProvider);
    }
}
