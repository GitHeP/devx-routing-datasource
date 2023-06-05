package com.github.devx.routing.config;

import com.github.devx.routing.RoutingTargetAttribute;
import com.github.devx.routing.RoutingTargetType;
import com.github.devx.routing.exception.InternalRuntimeException;
import com.github.devx.routing.loadbalance.LoadBalance;
import com.github.devx.routing.loadbalance.WeightRandomLoadBalance;
import com.github.devx.routing.loadbalance.ReadLoadBalanceType;
import com.github.devx.routing.loadbalance.WeightRoundRobinLoadBalance;
import com.github.devx.routing.loadbalance.WriteLoadBalanceType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Peng He
 * @since 1.0
 */

@Data
public class RoutingConfiguration {

    public static final String DATA_SOURCE_CLASS_NAME_KEY = "dataSourceClass";

    private List<DataSourceConfiguration> dataSources;

    private Set<String> masters;

    private Set<String> replicas;

    private ReadLoadBalanceType readLoadBalanceType;

    private WriteLoadBalanceType writeLoadBalanceType;

    private RoutingRuleConfiguration rules;

    public List<String> getDataSourceNames() {
        return dataSources.stream().map(DataSourceConfiguration::getName).collect(Collectors.toList());
    }


    public DataSourceConfiguration getDataSourceConfByName(final String name) {
        Optional<DataSourceConfiguration> optional = dataSources.stream().filter(dsConf -> Objects.equals(dsConf.getName(), name)).findFirst();
        return optional.orElse(null);
    }

    public List<RoutingTargetAttribute> getRoutingTargetAttributes(List<DataSourceConfiguration> dataSources) {
        List<RoutingTargetAttribute> attributes = new ArrayList<>();
        for (DataSourceConfiguration dataSourceConf : dataSources) {
            RoutingTargetAttribute attribute = dataSourceConf.getRoutingTargetAttribute();
            attributes.add(attribute);
        }
        return attributes;
    }

    public LoadBalance<RoutingTargetAttribute> makeReadLoadBalance(List<RoutingTargetAttribute> attributes) {

        LoadBalance<RoutingTargetAttribute> lb;
        ReadLoadBalanceType type = readLoadBalanceType;
        List<RoutingTargetAttribute> allReads = attributes.stream()
                .filter(attribute -> attribute.getRoutingTargetType().isRead())
                .collect(Collectors.toList());
        if (type == null) {
            return new WeightRandomLoadBalance(allReads);
        }

        List<RoutingTargetAttribute> onlyReads = filterByRoutingTargetType(attributes, RoutingTargetType.READ);
        List<RoutingTargetAttribute> readWrites = filterByRoutingTargetType(attributes, RoutingTargetType.READ_WRITE);

        // ugly code needs fix
        if (type == ReadLoadBalanceType.WEIGHT_RANDOM_BALANCE_ALL) {
            lb = new WeightRandomLoadBalance(allReads);
        } else if (type == ReadLoadBalanceType.WEIGHT_RANDOM_BALANCE_ONLY_READ) {
            lb = new WeightRandomLoadBalance(onlyReads);
        } else if (type == ReadLoadBalanceType.WEIGHT_RANDOM_BALANCE_READ_WRITE) {
            lb = new WeightRandomLoadBalance(readWrites);
        } else if (type == ReadLoadBalanceType.WEIGHT_ROUND_ROBIN_BALANCE_ALL) {
            lb = new WeightRoundRobinLoadBalance(allReads);
        } else if (type == ReadLoadBalanceType.WEIGHT_ROUND_ROBIN_BALANCE_ONLY_READ) {
            lb = new WeightRoundRobinLoadBalance(onlyReads);
        } else if (type == ReadLoadBalanceType.WEIGHT_ROUND_ROBIN_BALANCE_READ_WRITE) {
            lb = new WeightRoundRobinLoadBalance(readWrites);
        } else {
            throw new InternalRuntimeException(String.format("Unsupported load balancing type %s" , type));
        }
        return lb;
    }

    public LoadBalance<RoutingTargetAttribute> makeWriteLoadBalance(List<RoutingTargetAttribute> attributes) {

        LoadBalance<RoutingTargetAttribute> lb;
        WriteLoadBalanceType type = writeLoadBalanceType;
        List<RoutingTargetAttribute> allWrites = attributes.stream()
                .filter(attribute -> attribute.getRoutingTargetType().isWrite())
                .collect(Collectors.toList());
        if (type == null) {
            return new WeightRandomLoadBalance(allWrites);
        }

        List<RoutingTargetAttribute> onlyWrites = filterByRoutingTargetType(attributes, RoutingTargetType.WRITE);
        List<RoutingTargetAttribute> readWrites = filterByRoutingTargetType(attributes, RoutingTargetType.READ_WRITE);

        // ugly code needs fix
        if (type == WriteLoadBalanceType.RANDOM_BALANCE_ONLY_WRITE) {
            lb = new WeightRandomLoadBalance(onlyWrites);
        } else if (type == WriteLoadBalanceType.RANDOM_BALANCE_READ_WRITE) {
            lb = new WeightRandomLoadBalance(readWrites);
        } else if (type == WriteLoadBalanceType.ROUND_ROBIN_BALANCE_ONLY_WRITE) {
            lb = new WeightRoundRobinLoadBalance(onlyWrites);
        } else if (type == WriteLoadBalanceType.ROUND_ROBIN_BALANCE_READ_WRITE) {
            lb = new WeightRoundRobinLoadBalance(readWrites);
        } else {
            throw new InternalRuntimeException(String.format("Unsupported load balancing type %s" , type));
        }

        return lb;
    }

    private List<RoutingTargetAttribute> filterByRoutingTargetType(List<RoutingTargetAttribute> attributes , RoutingTargetType target) {
        return attributes.stream().filter(attribute -> Objects.equals(attribute.getRoutingTargetType() , target))
                .collect(Collectors.toList());
    }

}
