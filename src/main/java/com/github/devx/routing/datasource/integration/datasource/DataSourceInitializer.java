package com.github.devx.routing.datasource.integration.datasource;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @author he peng
 * @since 1.0
 */
public interface DataSourceInitializer {

    DataSource initialize(String dataSourceClassName , Map<String, Object> properties);
}
