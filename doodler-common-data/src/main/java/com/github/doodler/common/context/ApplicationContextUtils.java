package com.github.doodler.common.context;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import com.github.doodler.common.Constants;

/**
 * @Description: ApplicationContextUtils
 * @Author: Fred Feng
 * @Date: 15/11/2022
 * @Version 1.0.0
 */
@SuppressWarnings("unchecked")
@Component
public class ApplicationContextUtils
        implements ApplicationContextAware, BeanFactoryAware, EnvironmentAware, Ordered {

    private static final SpringContextHolder contextHolder = new SpringContextHolder();

    static class SpringContextHolder {

        ApplicationContext applicationContext;
        BeanFactory beanFactory;
        Environment environment;

        public ApplicationContext getApplicationContext() {
            Assert.notNull(applicationContext, "Nullable ApplicationContext.");
            return applicationContext;
        }

        public BeanFactory getBeanFactory() {
            Assert.notNull(beanFactory, "Nullable BeanFactory.");
            return beanFactory;
        }

        public Environment getEnvironment() {
            Assert.notNull(environment, "Nullable Environment.");
            return environment;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        contextHolder.applicationContext = applicationContext;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        contextHolder.beanFactory = beanFactory;
    }

    @Override
    public void setEnvironment(Environment environment) {
        contextHolder.environment = environment;
    }

    public static ApplicationContext getApplicationContext() {
        return contextHolder.getApplicationContext();
    }

    public static BeanFactory getBeanFactory() {
        return contextHolder.getBeanFactory();
    }

    public static Environment getEnvironment() {
        return contextHolder.getEnvironment();
    }

    public static <T> T getBean(String name) {
        return (T) getApplicationContext().getBean(name);
    }

    public static <T> T getBean(Class<T> requiredType) {
        return getApplicationContext().getBean(requiredType);
    }

    public static <T> T getBean(String name, Class<T> requiredType) {
        return getApplicationContext().getBean(name, requiredType);
    }

    public static String[] getBeanNames() {
        return getApplicationContext().getBeanDefinitionNames();
    }

    public static <T> T autowireBean(T object) {
        getApplicationContext().getAutowireCapableBeanFactory().autowireBean(object);
        return object;
    }

    public static String getRequiredProperty(String key) {
        return getEnvironment().getRequiredProperty(key);
    }

    public static <T> T getProperty(String key, Class<T> requiredType) {
        return getEnvironment().getProperty(key, requiredType);
    }

    public static <T> T getProperty(String key, Class<T> requiredType, T defaultValue) {
        return getEnvironment().getProperty(key, requiredType, defaultValue);
    }

    public static String getProperty(String key, String defaultValue) {
        return getEnvironment().getProperty(key, defaultValue);
    }

    public static String getProperty(String key) {
        return getEnvironment().getProperty(key);
    }

    public static <T extends ApplicationEvent> void publishEvent(T event) {
        getApplicationContext().publishEvent(event);
    }

    public static void setAware(Object bean) {
        if (bean instanceof ApplicationContextAware) {
            ((ApplicationContextAware) bean).setApplicationContext(getApplicationContext());
        }
        if (bean instanceof BeanFactoryAware) {
            ((BeanFactoryAware) bean).setBeanFactory(getBeanFactory());
        }
        if (bean instanceof EnvironmentAware) {
            ((EnvironmentAware) bean).setEnvironment(getEnvironment());
        }
    }

    public static boolean notProduction() {
        final String[] activeProfiles = getEnvironment().getActiveProfiles();
        return activeProfiles.length != 0 && !Constants.ENV_PROD.equals(activeProfiles[0]);
    }

    public static <T> T getOrCreateBean(Class<T> requiredType) {
        try {
            return getBean(requiredType);
        } catch (BeansException e) {
            return autowireBean(BeanUtils.instantiateClass(requiredType));
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}
