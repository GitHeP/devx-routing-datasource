package com.github.devx.routing.datasource.integration.datasource;

import com.github.devx.routing.datasource.exception.ConfigurationException;

import javax.sql.DataSource;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * @author he peng
 * @since 1.0
 */
public interface GenericDataSourceInitializer<T extends DataSource> extends DataSourceInitializer {

    default boolean supports(String dataSourceClassName) {

        boolean support = false;
        try {
            Class<?> datasourceType = Class.forName(dataSourceClassName);
            Type[] genericInterfaces = this.getClass().getGenericInterfaces();
            ParameterizedType parameterizedType = (ParameterizedType) genericInterfaces[0];
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            if (Objects.nonNull(typeArguments) && typeArguments.length == 1) {
                Class<?> actualType = (Class<?>) typeArguments[0];
                if (actualType.isAssignableFrom(datasourceType)) {
                    support = true;
                }
            }
        } catch (ClassNotFoundException e) {
            throw new ConfigurationException(e);
        }
        return support;
    }

}
