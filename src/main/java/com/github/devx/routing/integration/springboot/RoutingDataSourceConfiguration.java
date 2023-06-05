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

import com.github.devx.routing.config.DataSourceConfiguration;
import com.github.devx.routing.config.RoutingConfiguration;
import com.github.devx.routing.datasource.DataSourceWrapper;
import com.github.devx.routing.datasource.DefaultRoutingDataSource;
import com.github.devx.routing.datasource.RoutingContextRoutingKeyProvider;
import com.github.devx.routing.datasource.RoutingKeyProvider;
import com.github.devx.routing.exception.ConfigurationException;
import com.github.devx.routing.integration.datasource.CompositeDataSourceInitializer;
import com.github.devx.routing.integration.datasource.DataSourceInitializer;
import com.github.devx.routing.integration.datasource.GenericDataSourceInitializer;
import com.github.devx.routing.integration.mybatis.ExecutingSqlInterceptor;
import com.github.devx.routing.rule.RoutingRule;
import com.github.devx.routing.rule.group.BuiltInRoutingGroup;
import com.github.devx.routing.rule.group.CompositeRoutingGroup;
import com.github.devx.routing.rule.group.RoutingGroup;
import com.github.devx.routing.sql.parser.JSqlParser;
import com.github.devx.routing.sql.parser.SqlParser;
import org.apache.ibatis.plugin.Interceptor;
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

    @Bean
    public RoutingGroup<? extends RoutingRule> compositeRoutingGroup(@Autowired(required = false) List<RoutingGroup> routingGroups , RoutingProperties routingProperties, SqlParser sqlParser) {
        CompositeRoutingGroup compositeRoutingGroup = new CompositeRoutingGroup();
        compositeRoutingGroup.installFirst(routingGroups);
        compositeRoutingGroup.installLast(new BuiltInRoutingGroup(routingProperties.getRouting() , sqlParser));
        return compositeRoutingGroup;
    }

    @SuppressWarnings("unchecked")
    //@Bean
    //public RoutingRule compositeRoutingRule(SqlParser sqlParser, RoutingProperties properties , @Autowired(required = false) List<SqlAttributeRoutingRule> routingRules) {
    //
    //    RoutingConfiguration routing = properties.getRouting();
    //    sqlParser = new AnnotationSqlParser(sqlParser , new DefaultAnnotationSqlHintParser());
    //    Set<String> readDataSources = new HashSet<>();
    //    if (Objects.nonNull(routing.getMasters())) {
    //        readDataSources.addAll(routing.getReplicas());
    //    }
    //    Class<RandomLoadBalance> loadBalancerType = Objects.nonNull(routing.getReadLoadBalanceType()) ? (Class<RandomLoadBalance>) Reflect.onClass(properties.getLoadBalancer()).type() : RandomLoadBalance.class;
    //    LoadBalance<String> loadBalance = Reflect.onClass(loadBalancerType).create(new ArrayList<>(readDataSources)).get();
    //
    //    NullSqlAttributeRoutingRule unknownStatementRoutingRule = new NullSqlAttributeRoutingRule(sqlParser, loadBalance, properties.getWrites(), readDataSources);
    //    TxRoutingRule txRoutingRule = new TxRoutingRule(sqlParser, loadBalance, routing.getMasters(), readDataSources);
    //    ReadWriteSplittingRoutingRule readWriteSplittingRoutingRule = new ReadWriteSplittingRoutingRule(sqlParser, loadBalance, properties.getWrites(), readDataSources);
    //    ForceWriteRoutingRule forceWriteRoutingRule = new ForceWriteRoutingRule(sqlParser, loadBalance, properties.getWrites(), readDataSources);
    //    ForceReadRoutingRule forceReadRoutingRule = new ForceReadRoutingRule(sqlParser, loadBalance, properties.getWrites(), readDataSources);
    //    ForceTargetRoutingRule forceTargetRoutingRule = new ForceTargetRoutingRule();
    //    RoutingTypeAnnotationRoutingRule hintRoutingRule = new RoutingTypeAnnotationRoutingRule(sqlParser, loadBalance, properties.getWrites(), readDataSources);
    //    if (Objects.isNull(routingRules)) {
    //        routingRules = new ArrayList<>();
    //    }
    //    routingRules.add(unknownStatementRoutingRule);
    //    routingRules.add(txRoutingRule);
    //    routingRules.add(readWriteSplittingRoutingRule);
    //    routingRules.add(forceWriteRoutingRule);
    //    routingRules.add(forceReadRoutingRule);
    //    routingRules.add(forceTargetRoutingRule);
    //    routingRules.add(hintRoutingRule);
    //
    //    if (Objects.nonNull(properties.getRules()) && Objects.nonNull(properties.getRules().getTables())) {
    //        Map<String, Map<String, SqlTypeConfiguration>> tables = properties.getRules().getTables();
    //        TableRoutingRule tableRoutingRule = new TableRoutingRule(tables);
    //        routingRules.add(tableRoutingRule);
    //    }
    //
    //    return new CompositeRoutingRule(sqlParser , loadBalance, properties.getWrites(), readDataSources , routingRules);
    //}

    @Bean
    public DataSource routingDataSource(RoutingKeyProvider routingKeyProvider, RoutingProperties properties , DataSourceInitializer dataSourceInitializer , @Autowired @Qualifier("compositeRoutingGroup") RoutingRule routingRule) {

        if (Objects.isNull(properties.getRouting().getMasters()) || properties.getRouting().getMasters().isEmpty()) {
            throw new ConfigurationException("Configuration item [masters] is required");
        }

        RoutingConfiguration routing = properties.getRouting();

        Map<String , DataSource> dataSources = new HashMap<>();
        //for (Map.Entry<String, DataSourceConfiguration> entry : routing.getDataSources().entrySet()) {
        //    String name = entry.getKey();
        //    DataSourceConfiguration configuration = entry.getValue();
        //    Object dataSourceClassName = configuration.getDataSourceClass();
        //    if (Objects.isNull(dataSourceClassName)) {
        //        throw new ConfigurationException(String.format("Configuration item [%s] is required" , RoutingConfiguration.DATA_SOURCE_CLASS_NAME_KEY));
        //    }
        //    DataSource dataSource = dataSourceInitializer.initialize(dataSourceClassName.toString() , configuration.getProperties());
        //    dataSources.put(name , new DataSourceWrapper(dataSource , configuration.getRoutingTargetAttribute()));
        //}

        for (DataSourceConfiguration dataSourceConf : routing.getDataSources()) {
            Object dataSourceClassName = dataSourceConf.getDataSourceClass();
            if (Objects.isNull(dataSourceClassName)) {
                throw new ConfigurationException(String.format("Configuration item [%s] is required" , RoutingConfiguration.DATA_SOURCE_CLASS_NAME_KEY));
            }
            DataSource dataSource = dataSourceInitializer.initialize(dataSourceClassName.toString() , dataSourceConf.getProperties());
            dataSources.put(dataSourceConf.getName() , new DataSourceWrapper(dataSource , dataSourceConf.getRoutingTargetAttribute()));
        }


        return new DefaultRoutingDataSource(dataSources , routingRule , routingKeyProvider);
    }
}
