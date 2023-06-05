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
import java.util.Random;

/**
 * Random load balancing.
 *
 * @author Peng He
 * @since 1.0
 */
public class RandomLoadBalance implements LoadBalance<RoutingTargetAttribute> {

    private final List<RoutingTargetAttribute> options;
    private final Random random = new Random();

    public RandomLoadBalance(List<RoutingTargetAttribute> options) {
        this.options = options;
    }

    @Override
    public RoutingTargetAttribute choose() {
        if (options.isEmpty()) {
            return null;
        }
        int randomIndex = random.nextInt(options.size());
        return options.get(randomIndex);
    }
}
