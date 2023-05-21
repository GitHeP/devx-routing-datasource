package com.github.devx.routing.datasource.routing.loadbalance;

import java.util.List;

/**
 * @author he peng
 * @since 1.0
 */
public class RoundRobinLoadBalancer implements LoadBalancer<String> {

    private final List<String> options;
    private Integer current;

    public RoundRobinLoadBalancer(List<String> options) {
        this.options = options;
        this.current = 0;
    }

    @Override
    public String choose() {
        if (options.isEmpty()) {
            return null;
        }
        String chosen = options.get(current);
        current = (current + 1) % options.size();
        return chosen;
    }
}
