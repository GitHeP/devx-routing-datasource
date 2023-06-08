package com.github.devx.routing.config;

import lombok.Data;

import java.util.Map;

/**
 * @author Peng He
 * @since 1.0
 */

@Data
public class RoutingGroupPluggableConfiguration {

    private String groupName;

    private Boolean enable;

    private Map<String , Boolean> rules;
}
