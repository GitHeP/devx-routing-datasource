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
import com.github.devx.routing.config.DataSourceConfiguration;
import com.github.devx.routing.config.RoutingConfiguration;
import com.github.devx.routing.datasource.RoutingContext;
import com.github.devx.routing.loadbalance.LoadBalance;
import com.github.devx.routing.loadbalance.WeightRandomLoadBalance;
import com.github.devx.routing.sql.SqlAttribute;
import com.github.devx.routing.sql.parser.SqlParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Route to the specified data source forcefully,
 * and randomly select between multiple data sources
 * if more than one are specified.
 *
 * @author Peng He
 * @since 1.0
 *
 * @see RoutingContext#force(String...) ()
 * @see RoutingContext#forceRead()
 * @see RoutingContext#forceWrite()
 */
public class ForceTargetRoutingRule extends AbstractRoutingRule {

    private final RoutingConfiguration routingConf;

    public ForceTargetRoutingRule(RoutingConfiguration routingConf , SqlParser sqlParser, LoadBalance<RoutingTargetAttribute> readLoadBalance, LoadBalance<RoutingTargetAttribute> writeLoadBalance) {
        super(sqlParser, readLoadBalance, writeLoadBalance);
        this.routingConf = routingConf;
    }

    @Override
    public String routing(SqlAttribute sqlAttribute) {

        Set<String> forceDataSources = RoutingContext.getForceDataSources();
        if (forceDataSources != null && !forceDataSources.isEmpty()) {
            List<RoutingTargetAttribute> routingTargetAttributes = new ArrayList<>();
            for (String forceDataSource : forceDataSources) {
                DataSourceConfiguration dataSourceConf = routingConf.getDataSourceConfByName(forceDataSource);
                if (dataSourceConf != null) {
                    routingTargetAttributes.add(dataSourceConf.getRoutingTargetAttribute());
                }
            }

            if (!routingTargetAttributes.isEmpty()) {
                WeightRandomLoadBalance loadBalance = new WeightRandomLoadBalance(routingTargetAttributes);
                return loadBalance.choose().getName();
            }
        }

        if (RoutingContext.isForceWriteDataSource()) {
            return chooseWriteTargetName();
        }

        if (RoutingContext.isForceReadDataSource()) {
            return chooseReadTargetName();
        }

        return null;
    }

    @Override
    public int priority() {
        return 20;
    }



}
