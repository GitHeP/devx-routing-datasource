package com.github.devx.routing.datasource.integration.springboot;

import com.github.devx.routing.datasource.config.TableRuleConfiguration;
import com.github.devx.routing.datasource.exception.ConfigurationException;
import com.github.devx.routing.datasource.integration.datasource.CompositeDataSourceInitializer;
import com.github.devx.routing.datasource.integration.datasource.DataSourceInitializer;
import com.github.devx.routing.datasource.integration.datasource.GenericDataSourceInitializer;
import com.github.devx.routing.datasource.integration.mybatis.ExecutingSqlInterceptor;
import com.github.devx.routing.datasource.routing.DefaultRoutingDataSource;
import com.github.devx.routing.datasource.routing.RoutingContextRoutingKeyProvider;
import com.github.devx.routing.datasource.routing.RoutingKeyProvider;
import com.github.devx.routing.datasource.routing.loadbalance.LoadBalancer;
import com.github.devx.routing.datasource.routing.loadbalance.RandomLoadBalancer;
import com.github.devx.routing.datasource.routing.rule.AbstractRoutingRule;
import com.github.devx.routing.datasource.routing.rule.CompositeRoutingRule;
import com.github.devx.routing.datasource.routing.rule.ForceWriteRoutingRule;
import com.github.devx.routing.datasource.routing.rule.ReadWriteSplittingRoutingRule;
import com.github.devx.routing.datasource.routing.rule.RoutingRule;
import com.github.devx.routing.datasource.routing.rule.TableRoutingRule;
import com.github.devx.routing.datasource.routing.rule.TxRoutingRule;
import com.github.devx.routing.datasource.routing.rule.UnknownStatementRoutingRule;
import com.github.devx.routing.datasource.sql.parser.JSqlParser;
import com.github.devx.routing.datasource.sql.parser.SqlParser;
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
 * @author he peng
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
        //return new CalciteSqlParser();
    }

    @SuppressWarnings("unchecked")
    @Bean
    public RoutingRule compositeRoutingRule(SqlParser sqlParser, RoutingDataSourceProperties properties , @Autowired(required = false) List<AbstractRoutingRule> routingRules) {

        Set<String> readDataSources = new HashSet<>();
        if (Objects.nonNull(properties.getReadDataSources())) {
            readDataSources.addAll(properties.getReadDataSources());
        }
        Class<RandomLoadBalancer> loadBalancerType = Objects.nonNull(properties.getLoadBalancer()) ? (Class<RandomLoadBalancer>) Reflect.onClass(properties.getLoadBalancer()).type() : RandomLoadBalancer.class;
        LoadBalancer<String> loadBalancer = Reflect.onClass(loadBalancerType).create(new ArrayList<>(readDataSources)).get();

        UnknownStatementRoutingRule unknownStatementRoutingRule = new UnknownStatementRoutingRule(sqlParser, loadBalancer, properties.getWriteDataSource(), readDataSources);
        TxRoutingRule txRoutingRule = new TxRoutingRule(sqlParser, loadBalancer, properties.getWriteDataSource(), readDataSources);
        ReadWriteSplittingRoutingRule readWriteSplittingRoutingRule = new ReadWriteSplittingRoutingRule(sqlParser, loadBalancer, properties.getWriteDataSource(), readDataSources);
        ForceWriteRoutingRule forceWriteRoutingRule = new ForceWriteRoutingRule(sqlParser, loadBalancer, properties.getWriteDataSource(), readDataSources);
        if (Objects.isNull(routingRules)) {
            routingRules = new ArrayList<>();
        }
        routingRules.add(unknownStatementRoutingRule);
        routingRules.add(txRoutingRule);
        routingRules.add(readWriteSplittingRoutingRule);
        routingRules.add(forceWriteRoutingRule);

        if (Objects.nonNull(properties.getRules()) && Objects.nonNull(properties.getRules().getTables())) {
            List<Map<String, TableRuleConfiguration>> tableRules = properties.getRules().getTables();
            List<TableRuleConfiguration> tableRuleConfigurations = new ArrayList<>();
            for (Map<String, TableRuleConfiguration> tableRule : tableRules) {
                for (Map.Entry<String, TableRuleConfiguration> entry : tableRule.entrySet()) {
                    Set<String> tables = new HashSet<>();
                    tables.add(entry.getKey());
                    entry.getValue().setTables(tables);
                    tableRuleConfigurations.add(entry.getValue());
                }
            }
            TableRoutingRule tableRoutingRule = new TableRoutingRule(sqlParser, loadBalancer, properties.getWriteDataSource(), readDataSources, tableRuleConfigurations);
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
        for (Map.Entry<String, Map<String, Object>> entry : properties.getDataSources().entrySet()) {
            String name = entry.getKey();
            Map<String, Object> property = entry.getValue();
            Object dataSourceClassName = property.get(RoutingDataSourceProperties.DATA_SOURCE_CLASS_NAME_KEY);
            if (Objects.isNull(dataSourceClassName)) {
                throw new ConfigurationException(String.format("Configuration item [%s] is required" , RoutingDataSourceProperties.DATA_SOURCE_CLASS_NAME_KEY));
            }
            DataSource dataSource = dataSourceInitializer.initialize(dataSourceClassName.toString() , property);
            dataSources.put(name , dataSource);
        }

        return new DefaultRoutingDataSource(dataSources , routingRule , routingKeyProvider);
    }
}
