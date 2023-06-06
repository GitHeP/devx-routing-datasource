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

package com.lp.sql.routing.loadbalance;

import com.lp.sql.routing.RoutingTargetAttribute;

import java.util.List;
import java.util.Random;

/**
 * Random load balancing.
 *
 * @author Peng He
 * @since 1.0
 */
public class WeightRandomLoadBalance implements LoadBalance<RoutingTargetAttribute> {

    private final List<RoutingTargetAttribute> options;

    public WeightRandomLoadBalance(List<RoutingTargetAttribute> options) {
        this.options = options;
    }

    @Override
    public RoutingTargetAttribute choose() {

        int totalWeight = 0;
        for (RoutingTargetAttribute target : options) {
            totalWeight += target.getWeight();
        }

        int randomWeight = new Random().nextInt(totalWeight) + 1;
        int currentWeight = 0;
        RoutingTargetAttribute chosen = null;
        for (RoutingTargetAttribute attribute : options) {
            currentWeight += attribute.getWeight();
            if (randomWeight <= currentWeight) {
                chosen = attribute;
            }
        }
        return chosen;
    }
}
