package com.github.devx.routing.datasource.routing;

import com.github.devx.routing.datasource.routing.jdbc.RoutingConnection;

import java.sql.Connection;
import java.util.Objects;

/**
 * @author he peng
 * @since 1.0
 */
public abstract class RoutingUtils {

    private RoutingUtils() {
        throw new IllegalAccessError("RoutingUtils does not support object instantiation");
    }

    public static boolean isRoutingRead(Connection connection) {
        return isRoutingTarget(DataSourceMode.READ , connection);
    }

    public static boolean isRoutingWrite(Connection connection) {
        return isRoutingTarget(DataSourceMode.WRITE , connection);
    }

    public static boolean isRoutingTarget(DataSourceMode targetMode , Connection connection) {
        if (Objects.isNull(connection)) {
            return false;
        }

        if (connection instanceof RoutingConnection) {
            RoutingConnection rc = (RoutingConnection) connection;
            DataSourceMode dataSourceMode = rc.getDataSourceMode();
            if (Objects.isNull(dataSourceMode)) {
                return false;
            }
            return Objects.equals(targetMode, dataSourceMode) || Objects.equals(DataSourceMode.READ_WRITE, dataSourceMode);
        }
        return false;
    }
}
