package com.github.devx.routing.datasource.integration.springboot;

import com.github.devx.routing.datasource.routing.RoutingContext;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.BeanFactoryTransactionAttributeSourceAdvisor;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author he peng
 * @since 1.0
 */
public class TxDetectionBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        if (bean instanceof Advised) {
            Advised advised = (Advised) bean;
            Advisor[] advisors = advised.getAdvisors();
            boolean tx = false;
            for (Advisor advisor : advisors) {
                if (BeanFactoryTransactionAttributeSourceAdvisor.class.isAssignableFrom(advisor.getClass())) {
                    tx = true;
                    break;
                }
            }
            if (tx) {
                advised.addAdvice(advisors.length , new RoutingContextInterceptor());
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    private static class RoutingContextInterceptor implements MethodInterceptor {

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            try {
                Method method = invocation.getMethod();
                Class<?> targetClass = AopProxyUtils.ultimateTargetClass(invocation.getThis());
                Transactional methodTx = AnnotationUtils.findAnnotation(method, Transactional.class);
                Transactional typeTx = AnnotationUtils.findAnnotation(targetClass, Transactional.class);
                if (Objects.nonNull(methodTx) || Objects.nonNull(typeTx)) {
                    RoutingContext.setInTx();
                    RoutingContext.setTxReadOnly(getReadOnly(methodTx , typeTx));
                }
                return invocation.proceed();
            } finally {
                RoutingContext.clear();
            }
        }

        private boolean getReadOnly(Transactional methodTx , Transactional typeTx) {

            boolean readOnly = false;
            if (Objects.nonNull(typeTx)) {
                readOnly = typeTx.readOnly();
            }
            if (Objects.nonNull(methodTx)) {
                readOnly = methodTx.readOnly();
            }
            return readOnly;
        }
    }

}
