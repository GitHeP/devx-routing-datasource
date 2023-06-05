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

package com.github.devx.routing.loadbalance;

import com.github.devx.routing.RoutingTargetAttribute;

import java.util.List;

/**
 * Round-robin load balancing.
 *
 * @author Peng He
 * @since 1.0
 */
public class RoundRobinLoadBalance implements LoadBalance<RoutingTargetAttribute> {

    private final List<RoutingTargetAttribute> options;
    private Integer current;

    public RoundRobinLoadBalance(List<RoutingTargetAttribute> options) {
        this.options = options;
        this.current = 0;
    }

    @Override
    public RoutingTargetAttribute choose() {
        if (options.isEmpty()) {
            return null;
        }
        RoutingTargetAttribute chosen = options.get(current);
        current = (current + 1) % options.size();
        return chosen;
    }
}
