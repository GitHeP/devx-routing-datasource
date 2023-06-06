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

package com.lp.sql.routing.util;

import com.lp.sql.routing.RoutingTargetType;
import com.lp.sql.routing.jdbc.RoutingConnection;

import java.sql.Connection;
import java.util.Objects;

/**
 * @author Peng He
 * @since 1.0
 */
public abstract class RoutingUtils {

    private RoutingUtils() {
        throw new IllegalAccessError("RoutingUtils does not support object instantiation");
    }

    public static boolean isRoutingRead(Connection connection) {
        return isRoutingTarget(RoutingTargetType.READ , connection);
    }

    public static boolean isRoutingWrite(Connection connection) {
        return isRoutingTarget(RoutingTargetType.WRITE , connection);
    }

    public static boolean isRoutingTarget(RoutingTargetType targetMode , Connection connection) {
        if (Objects.isNull(connection)) {
            return false;
        }

        if (connection instanceof RoutingConnection) {
            RoutingConnection rc = (RoutingConnection) connection;
            RoutingTargetType routingTargetType = rc.getRoutingTargetType();
            if (Objects.isNull(routingTargetType)) {
                return false;
            }
            return Objects.equals(targetMode, routingTargetType) || Objects.equals(RoutingTargetType.READ_WRITE, routingTargetType);
        }
        return false;
    }
}
