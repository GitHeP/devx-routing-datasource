package com.github.devx.routing.loadbalance;

/**
 * @author Peng He
 * @since 1.0
 */
public enum WriteLoadBalanceType {

    /**
     *
     */
    RANDOM_BALANCE_ONLY_WRITE,

    RANDOM_BALANCE_READ_WRITE,

    ROUND_ROBIN_BALANCE_ONLY_WRITE,

    ROUND_ROBIN_BALANCE_READ_WRITE,

    WEIGHT_BALANCE_ONLY_WRITE,

    WEIGHT_BALANCE_READ_WRITE,

}
