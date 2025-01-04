package com.github.doodler.common.context;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ClassUtils;
import com.github.doodler.common.utils.ExceptionUtils;

/**
 * 
 * @Description: BeanReflectionService
 * @Author: Fred Feng
 * @Date: 30/12/2024
 * @Version 1.0.0
 */
public class BeanReflectionService implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    public Object invokeTargetMethod(String className, String beanName, String methodName,
            Object[] arguments) {
        Class<?> clz = null;
        if (StringUtils.isNotBlank(className)) {
            try {
                clz = ClassUtils.forName(className, Thread.currentThread().getContextClassLoader());
            } catch (Throwable e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
        return invokeTargetMethod(clz, beanName, methodName, arguments);
    }

    public Object invokeTargetMethod(Class<?> clz, String beanName, String methodName,
            Object[] arguments) {
        Object targetBean = lookupTargetBean(clz, beanName);
        try {
            return MethodUtils.invokeExactMethod(targetBean, methodName, arguments);
        } catch (Throwable e) {
            e = ExceptionUtils.getOriginalException(e);
            throw new MethodInvocationException(e.getMessage(), e);
        }
    }

    public Object invokeTargetMethod(String className, String beanName, String methodName,
            Object[] arguments, Class<?>[] argumentClasses) {
        Class<?> clz = null;
        if (StringUtils.isNotBlank(className)) {
            try {
                clz = ClassUtils.forName(className, Thread.currentThread().getContextClassLoader());
            } catch (Throwable e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
        return invokeTargetMethod(clz, beanName, methodName, arguments, argumentClasses);
    }

    public Object invokeTargetMethod(Class<?> clz, String beanName, String methodName,
            Object[] arguments, Class<?>[] argumentClasses) {
        Object targetBean = lookupTargetBean(clz, beanName);
        try {
            return MethodUtils.invokeExactMethod(targetBean, methodName, arguments,
                    argumentClasses);
        } catch (Throwable e) {
            e = ExceptionUtils.getOriginalException(e);
            throw new MethodInvocationException(e.getMessage(), e);
        }
    }

    private Object lookupTargetBean(Class<?> clz, String beanName) {
        if (clz != null && StringUtils.isNotBlank(beanName)) {
            return applicationContext.getBean(beanName, clz);
        } else if (clz != null && StringUtils.isBlank(beanName)) {
            return applicationContext.getBean(clz);
        } else if (clz == null && StringUtils.isNotBlank(beanName)) {
            return applicationContext.getBean(beanName);
        }
        return lookupTargetBeanIfNecessary(clz, beanName);
    }

    protected Object lookupTargetBeanIfNecessary(Class<?> clz, String beanName) {
        throw new NoSuchBeanDefinitionException("Null", "Null className or beanName");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

}
