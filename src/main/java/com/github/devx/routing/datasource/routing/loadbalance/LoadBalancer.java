package com.github.devx.routing.datasource.routing.loadbalance;

/**
 * @author he peng
 * @since 1.0
 */
public interface LoadBalancer<T> {

    T choose();
}
