package com.lp.sql.routing.loadbalance;

/**
 * @author Peng He
 * @since 1.0
 */
public enum ReadLoadBalanceType {

    /**
     *
     */
    WEIGHT_RANDOM_BALANCE_ALL,

    /**
     * 
     */
    WEIGHT_RANDOM_BALANCE_ONLY_READ,

    /**
     *
     */
    WEIGHT_RANDOM_BALANCE_READ_WRITE,

    WEIGHT_ROUND_ROBIN_BALANCE_ALL,

    WEIGHT_ROUND_ROBIN_BALANCE_ONLY_READ,

    WEIGHT_ROUND_ROBIN_BALANCE_READ_WRITE,

}
