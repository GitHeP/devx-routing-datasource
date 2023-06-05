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

import com.github.devx.routing.RoutingTargetType;
import com.github.devx.routing.config.DataSourceConfiguration;
import com.github.devx.routing.config.SqlTypeConfiguration;
import com.github.devx.routing.datasource.DataSourceAttribute;
import com.github.devx.routing.datasource.DataSourceWrapper;
import com.github.devx.routing.datasource.DefaultRoutingDataSource;
import com.github.devx.routing.datasource.RoutingContextRoutingKeyProvider;
import com.github.devx.routing.datasource.RoutingKeyProvider;
import com.github.devx.routing.exception.ConfigurationException;
import com.github.devx.routing.integration.datasource.CompositeDataSourceInitializer;
import com.github.devx.routing.integration.datasource.DataSourceInitializer;
import com.github.devx.routing.integration.datasource.GenericDataSourceInitializer;
import com.github.devx.routing.integration.mybatis.ExecutingSqlInterceptor;
import com.github.devx.routing.loadbalance.LoadBalancer;
import com.github.devx.routing.loadbalance.RandomLoadBalancer;
import com.github.devx.routing.rule.CompositeRoutingRule;
import com.github.devx.routing.rule.ForceReadRoutingRule;
import com.github.devx.routing.rule.ForceTargetRoutingRule;
import com.github.devx.routing.rule.ForceWriteRoutingRule;
import com.github.devx.routing.rule.ReadWriteSplittingRoutingRule;
import com.github.devx.routing.rule.RoutingRule;
import com.github.devx.routing.rule.RoutingTypeAnnotationRoutingRule;
import com.github.devx.routing.rule.SqlAttributeRoutingRule;
import com.github.devx.routing.rule.TableRoutingRule;
import com.github.devx.routing.rule.TxRoutingRule;
import com.github.devx.routing.rule.UnknownSqlAttributeRoutingRule;
import com.github.devx.routing.sql.parser.AnnotationSqlParser;
import com.github.devx.routing.sql.parser.DefaultAnnotationSqlHintParser;
import com.github.devx.routing.sql.parser.JSqlParser;
import com.github.devx.routing.sql.parser.SqlParser;
import org.apache.ibatis.plugin.Interceptor;
import org.joor.Reflect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Peng He
 * @since 1.0
 */

@EnableConfigurationProperties(RoutingDataSourceProperties.class)
public class RoutingDataSourceConfiguration {

    @Bean
    public DataSourceInitializer compositeDataSourceInitializer(@Autowired(required = false) Set<GenericDataSourceInitializer<?>> initializers) {
        return new CompositeDataSourceInitializer(initializers);
    }

    @Bean
    public TxDetectionBeanPostProcessor txBeanPostProcessor() {
        return new TxDetectionBeanPostProcessor();
    }

    @Bean
    public Interceptor executingSqlInterceptor() {
        return new ExecutingSqlInterceptor();
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

    @SuppressWarnings("unchecked")
    @Bean
    public RoutingRule compositeRoutingRule(SqlParser sqlParser, RoutingDataSourceProperties properties , @Autowired(required = false) List<SqlAttributeRoutingRule> routingRules) {

        sqlParser = new AnnotationSqlParser(sqlParser , new DefaultAnnotationSqlHintParser());
        Set<String> readDataSources = new HashSet<>();
        if (Objects.nonNull(properties.getReadDataSources())) {
            readDataSources.addAll(properties.getReadDataSources());
        }
        Class<RandomLoadBalancer> loadBalancerType = Objects.nonNull(properties.getLoadBalancer()) ? (Class<RandomLoadBalancer>) Reflect.onClass(properties.getLoadBalancer()).type() : RandomLoadBalancer.class;
        LoadBalancer<String> loadBalancer = Reflect.onClass(loadBalancerType).create(new ArrayList<>(readDataSources)).get();

        UnknownSqlAttributeRoutingRule unknownStatementRoutingRule = new UnknownSqlAttributeRoutingRule(sqlParser, loadBalancer, properties.getWriteDataSource(), readDataSources);
        TxRoutingRule txRoutingRule = new TxRoutingRule(sqlParser, loadBalancer, properties.getWriteDataSource(), readDataSources);
        ReadWriteSplittingRoutingRule readWriteSplittingRoutingRule = new ReadWriteSplittingRoutingRule(sqlParser, loadBalancer, properties.getWriteDataSource(), readDataSources);
        ForceWriteRoutingRule forceWriteRoutingRule = new ForceWriteRoutingRule(sqlParser, loadBalancer, properties.getWriteDataSource(), readDataSources);
        ForceReadRoutingRule forceReadRoutingRule = new ForceReadRoutingRule(sqlParser, loadBalancer, properties.getWriteDataSource(), readDataSources);
        ForceTargetRoutingRule forceTargetRoutingRule = new ForceTargetRoutingRule();
        RoutingTypeAnnotationRoutingRule hintRoutingRule = new RoutingTypeAnnotationRoutingRule(sqlParser, loadBalancer, properties.getWriteDataSource(), readDataSources);
        if (Objects.isNull(routingRules)) {
            routingRules = new ArrayList<>();
        }
        routingRules.add(unknownStatementRoutingRule);
        routingRules.add(txRoutingRule);
        routingRules.add(readWriteSplittingRoutingRule);
        routingRules.add(forceWriteRoutingRule);
        routingRules.add(forceReadRoutingRule);
        routingRules.add(forceTargetRoutingRule);
        routingRules.add(hintRoutingRule);

        if (Objects.nonNull(properties.getRules()) && Objects.nonNull(properties.getRules().getTables())) {
            Map<String, Map<String, SqlTypeConfiguration>> tables = properties.getRules().getTables();
            TableRoutingRule tableRoutingRule = new TableRoutingRule(tables);
            routingRules.add(tableRoutingRule);
        }

        return new CompositeRoutingRule(sqlParser , loadBalancer , properties.getWriteDataSource(), readDataSources , routingRules);
    }

    @Bean
    public DataSource routingDataSource(RoutingKeyProvider routingKeyProvider, RoutingDataSourceProperties properties , DataSourceInitializer dataSourceInitializer , @Autowired @Qualifier("compositeRoutingRule") RoutingRule routingRule) {

        if (Objects.isNull(properties.getWriteDataSource())) {
            throw new ConfigurationException("Configuration item [writeDataSource] is required");
        }

        Map<String , DataSource> dataSources = new HashMap<>();
        for (Map.Entry<String, DataSourceConfiguration> entry : properties.getDataSources().entrySet()) {
            String name = entry.getKey();
            DataSourceConfiguration configuration = entry.getValue();
            Object dataSourceClassName = configuration.getDataSourceClass();
            if (Objects.isNull(dataSourceClassName)) {
                throw new ConfigurationException(String.format("Configuration item [%s] is required" , RoutingDataSourceProperties.DATA_SOURCE_CLASS_NAME_KEY));
            }
            DataSource dataSource = dataSourceInitializer.initialize(dataSourceClassName.toString() , configuration.getProperties());
            RoutingTargetType type = RoutingTargetType.READ;
            if (properties.getWriteDataSource().equals(name)) {
                type = RoutingTargetType.READ_WRITE;
            }
            DataSourceAttribute dataSourceAttribute = new DataSourceAttribute(type, name, configuration.getWeight());
            dataSources.put(name , new DataSourceWrapper(dataSource , dataSourceAttribute));
        }

        return new DefaultRoutingDataSource(dataSources , routingRule , routingKeyProvider);
    }
}
