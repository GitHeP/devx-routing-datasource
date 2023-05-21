package com.github.devx.routing.datasource.routing.rule;

/**
 * Support routing rules that specify execution priority.
 * Sort by priority from small to large, with smaller numbers indicating higher priority.
 *
 * @author he peng
 * @since 1.0
 */
public interface PriorityRoutingRule extends RoutingRule {


    /**
     * specify priority , the smaller the number, the higher the priority.
     * @return Within the minimum and maximum value range of int
     */
    int priority();
}
