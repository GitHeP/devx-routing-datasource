package com.github.devx.routing.datasource.routing.loadbalance;

import java.util.List;
import java.util.Random;

/**
 * Random load balancing.
 *
 * @author he peng
 * @since 1.0
 */
public class RandomLoadBalancer implements LoadBalancer<String> {

    private final List<String> options;
    private final Random random = new Random();

    public RandomLoadBalancer(List<String> options) {
        this.options = options;
    }

    @Override
    public String choose() {
        if (options.isEmpty()) {
            return null;
        }
        int randomIndex = random.nextInt(options.size());
        return options.get(randomIndex);
    }
}
