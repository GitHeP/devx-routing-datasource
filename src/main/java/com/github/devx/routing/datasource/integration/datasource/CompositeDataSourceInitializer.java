package com.github.devx.routing.datasource.integration.datasource;

import com.github.devx.routing.datasource.exception.ConfigurationException;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author he peng
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
