package com.github.devx.routing.datasource.integration.mybatis;

import com.github.devx.routing.datasource.routing.RoutingContext;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * @author he peng
 * @since 1.0
 */

@Intercepts({@Signature(
        type = Executor.class,
        method = "update",
        args = {MappedStatement.class, Object.class}
), @Signature(
        type = Executor.class,
        method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}
), @Signature(
        type = Executor.class,
        method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}
), @Signature(
        type = Executor.class,
        method = "queryCursor",
        args = {MappedStatement.class, Object.class, RowBounds.class}
)})
public class ExecutingSqlInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        try {
            Object[] args = invocation.getArgs();
            MappedStatement ms = (MappedStatement)args[0];
            Object parameter = args[1];
            BoundSql boundSql = ms.getBoundSql(parameter);
            String sql = boundSql.getSql();
            RoutingContext.addCurrentlyExecutingSql(sql);
            return invocation.proceed();
        } finally {
            if (!RoutingContext.inTx() && !RoutingContext.getForceWriteDataSource()) {
                RoutingContext.clear();
            }
        }
    }
}
