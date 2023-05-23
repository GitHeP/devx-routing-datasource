package com.github.devx.routing.datasource.routing.rule;

import com.github.devx.routing.datasource.routing.RoutingKey;
import com.github.devx.routing.datasource.routing.loadbalance.LoadBalancer;
import com.github.devx.routing.datasource.sql.parser.SqlParser;
import com.github.devx.routing.datasource.sql.parser.SqlStatement;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author he peng
 * @since 1.0
 */
public abstract class AbstractRoutingRule implements StatementRoutingRule {

    protected final SqlParser sqlParser;

    protected final LoadBalancer<String> loadBalancer;

    protected final String writeDataSourceName;

    protected final Set<String> readDataSourceNames;

    protected AbstractRoutingRule(SqlParser sqlParser , LoadBalancer<String> loadBalancer , String writeDataSourceName , Set<String> readDataSourceNames) {
        this.sqlParser = sqlParser;
        this.loadBalancer = loadBalancer;
        this.writeDataSourceName = writeDataSourceName;
        if (Objects.isNull(readDataSourceNames)) {
            readDataSourceNames = new HashSet<>();
        }
        this.readDataSourceNames = Collections.synchronizedSet(readDataSourceNames);
    }

    @Override
    public String routing(RoutingKey key) {
        SqlStatement statement = null;
        if (Objects.nonNull(key) && Objects.nonNull(key.getSql())) {
            statement = sqlParser.parse(key.getSql());
        }
        return routing(statement);
    }

}
