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

/**
 * Round-robin load balancing.
 *
 * @author Peng He
 * @since 1.0
 */
public class WeightRoundRobinLoadBalance implements LoadBalance<RoutingTargetAttribute> {

    private final List<RoutingTargetAttribute> options;
    private Integer currentIndex;
    private Integer currentWeight;
    private Integer maxWeight;
    private Integer gcdWeight;

    public WeightRoundRobinLoadBalance(List<RoutingTargetAttribute> options) {
        this.options = options;
        this.currentIndex = -1;
        this.currentWeight = 0;
        this.maxWeight = 0;
        this.gcdWeight = 0;
    }

    @Override
    public RoutingTargetAttribute choose() {
        RoutingTargetAttribute chosen;

        for (RoutingTargetAttribute target : options) {
            int weight = target.getWeight();
            maxWeight = Math.max(maxWeight, weight);
            gcdWeight = gcd(gcdWeight, weight);
        }

        while (true) {
            currentIndex = (currentIndex + 1) % options.size();
            if (currentIndex == 0) {
                currentWeight = currentWeight - gcdWeight;
                if (currentWeight <= 0) {
                    currentWeight = maxWeight;
                    if (currentWeight == 0) {
                        return null;
                    }
                }
            }
            RoutingTargetAttribute target = options.get(currentIndex);
            if (target.getWeight() >= currentWeight) {
                chosen = target;
                break;
            }
        }
        return chosen;
    }

    private static int gcd(int a, int b) {
        if (b == 0) {
            return a;
        } else {
            return gcd(b, a % b);
        }
    }
}
