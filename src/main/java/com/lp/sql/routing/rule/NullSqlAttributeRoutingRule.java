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

package com.lp.sql.routing.rule;

import com.lp.sql.routing.RoutingTargetAttribute;
import com.lp.sql.routing.loadbalance.LoadBalance;
import com.lp.sql.routing.sql.SqlAttribute;
import com.lp.sql.routing.sql.parser.SqlParser;

import java.util.Objects;

/**
 * When the SQL cannot be parsed by the SqlParser,
 * data source routing based on the SQL cannot be performed.
 * It will be routed to the write data source by default.
 *
 * @author Peng He
 * @since 1.0
 *
 * @see SqlParser
 */
public class NullSqlAttributeRoutingRule extends AbstractRoutingRule {

    public NullSqlAttributeRoutingRule(SqlParser sqlParser, LoadBalance<RoutingTargetAttribute> readLoadBalance, LoadBalance<RoutingTargetAttribute> writeLoadBalance) {
        super(sqlParser, readLoadBalance, writeLoadBalance);
    }

    @Override
    public String routing(SqlAttribute attribute) {
        return Objects.isNull(attribute) ? chooseWriteTargetName() : null;
    }

    @Override
    public int priority() {
        return 10;
    }
}
