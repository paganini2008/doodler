package com.github.doodler.common.redis.eventbus;

import java.lang.reflect.Method;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: SubscriberBeanPostProcessor
 * @Author: Fred Feng
 * @Date: 09/03/2023
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class SubscriberBeanPostProcessor implements BeanPostProcessor {

    private final RedisEventBus eventBus;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof EventSubscriber) {
        	eventBus.register((EventSubscriber) bean);
        }
        final Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
        Method[] methodArray = MethodUtils.getMethodsWithAnnotation(targetClass, Subscriber.class);
        if (ArrayUtils.isNotEmpty(methodArray)) {
            for (Method method : methodArray) {
            	eventBus.register(new InternalEventSubscriber(bean, method));
                if (log.isInfoEnabled()) {
                    log.info("Registered eventbus subscriber: {}#{}", targetClass.getName(), method.toString());
                }
            }
        }
        return bean;
    }

    @RequiredArgsConstructor
    private static class InternalEventSubscriber implements EventSubscriber {

        private final Object target;
        private final Method method;

        @Override
        public boolean supports(Class<?> eventClass) {
            return eventClass.equals(method.getParameterTypes()[0]);
        }

        @SneakyThrows
        @Override
        public void fireEvent(Object event) {
            try {
                method.invoke(target, event);
            } catch (Exception ignored) {
                MethodUtils.invokeMethod(target, true, method.getName(), event);
            }
        }
    }
}