package com.github.doodler.common.amqp.eventbus;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: EventBusListenerBeanProcessor
 * @Author: Fred Feng
 * @Date: 13/01/2023
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class EventBusListenerBeanProcessor implements BeanPostProcessor {

    private final EventBus eventBus;
    private final ClassMapper classMapper;

    @Override
    public synchronized Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
        List<Method> annotatedMethods = MethodUtils.getMethodsListWithAnnotation(targetClass, Subscribe.class);
        if (CollectionUtils.isNotEmpty(annotatedMethods)) {

            annotatedMethods.forEach(m -> {
                if (m.getParameterCount() == 1) {
                    Parameter parameter = m.getParameters()[0];
                    EventClass eventClass = parameter.getAnnotation(EventClass.class);
                    Class<?> parameterType = parameter.getType();
                    if (eventClass != null) {
                        classMapper.registerTypeName(eventClass.value(), parameterType.getName());
                    }else {
                    	classMapper.registerTypeName(parameterType.getName(), parameterType.getName());
                    }
                } else {
                	throw new IllegalStateException("Invalid EventListener method: " + m.toString());
                }
            });
            eventBus.register(bean);
            if(log.isInfoEnabled()) {
            	annotatedMethods.forEach(m->{
            		log.info("Subscriber method ==> {}", m.toGenericString());
            	});
            }
        }
        return bean;
    }
}