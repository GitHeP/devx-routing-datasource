package com.github.devx.routing.datasource.integration.datasource;

import org.joor.Reflect;
import org.springframework.format.support.DefaultFormattingConversionService;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @author he peng
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
