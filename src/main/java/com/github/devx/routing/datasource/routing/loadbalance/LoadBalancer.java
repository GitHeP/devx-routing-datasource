package com.github.devx.routing.datasource.routing.loadbalance;

/**
 * Load balancing among multiple data sources.
 *
 * @author he peng
 * @since 1.0
 */
public interface LoadBalancer<T> {

    /**
     * Choose an option that fits the load balancing algorithm.
     * @return Generic instance
     */
    T choose();
}
