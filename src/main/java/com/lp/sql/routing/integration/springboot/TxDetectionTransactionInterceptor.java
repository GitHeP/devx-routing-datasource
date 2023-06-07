package com.lp.sql.routing.integration.springboot;

import com.lp.sql.routing.RoutingContext;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import java.lang.reflect.Method;

/**
 * @author Peng He
 * @since 1.0
 */

@Deprecated
public class TxDetectionTransactionInterceptor extends TransactionInterceptor {

    public TxDetectionTransactionInterceptor(TransactionManager ptm, TransactionAttributeSource tas) {
        super(ptm, tas);
    }

    @Override
    protected Object invokeWithinTransaction(Method method, Class<?> targetClass, InvocationCallback invocation) throws Throwable {
        try {
            RoutingContext.setInTx();
            TransactionAttributeSource tas = this.getTransactionAttributeSource();
            TransactionAttribute txAttr = tas != null ? tas.getTransactionAttribute(method, targetClass) : null;
            if (txAttr != null) {
                RoutingContext.setTxReadOnly(txAttr.isReadOnly());
            }
            return super.invokeWithinTransaction(method, targetClass, invocation);
        } finally {
            RoutingContext.clear();
        }
    }
}
