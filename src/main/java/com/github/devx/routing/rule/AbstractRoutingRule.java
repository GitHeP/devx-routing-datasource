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

package com.github.devx.routing.rule;

import com.github.devx.routing.RoutingTargetAttribute;
import com.github.devx.routing.loadbalance.LoadBalance;
import com.github.devx.routing.sql.SqlAttribute;
import com.github.devx.routing.sql.parser.SqlParser;

import java.util.Objects;

/**
 * @author Peng He
 * @since 1.0
 */
public abstract class AbstractRoutingRule implements SqlAttributeRoutingRule {

    protected final SqlParser sqlParser;

    protected final LoadBalance<RoutingTargetAttribute> readLoadBalance;

    protected final LoadBalance<RoutingTargetAttribute> writeLoadBalance;

    protected AbstractRoutingRule(SqlParser sqlParser , LoadBalance<RoutingTargetAttribute> readLoadBalance , LoadBalance<RoutingTargetAttribute> writeLoadBalance) {
        this.sqlParser = sqlParser;
        this.readLoadBalance = readLoadBalance;
        this.writeLoadBalance = writeLoadBalance;
    }

    @Override
    public String routing(RoutingKey key) {
        SqlAttribute sqlAttribute = null;
        if (Objects.nonNull(key) && Objects.nonNull(key.getSql())) {
            sqlAttribute = sqlParser.parse(key.getSql());
        }
        return routing(sqlAttribute);
    }

    protected String chooseWriteTargetName() {
        return writeLoadBalance.choose().getName();
    }

    protected String chooseReadTargetName() {
        return readLoadBalance.choose().getName();
    }

}
