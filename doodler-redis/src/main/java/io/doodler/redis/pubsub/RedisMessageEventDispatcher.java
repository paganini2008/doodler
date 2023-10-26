package io.doodler.redis.pubsub;

import java.lang.reflect.Method;
import java.util.Observable;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.event.EventListener;
import org.springframework.lang.Nullable;
import org.springframework.util.StringValueResolver;

import io.doodler.common.utils.MatchMode;
import io.doodler.common.utils.MutableObservable;
import io.doodler.common.utils.MutableObserver;

/**
 * @Description: RedisMessageEventDispatcher
 * @Author: Fred Feng
 * @Date: 14/12/2022
 * @Version 1.0.0
 */
@Slf4j
public class RedisMessageEventDispatcher implements BeanPostProcessor, EmbeddedValueResolverAware {

    private final MutableObservable repeableObs = new MutableObservable(true);
    private final MutableObservable unrepeableObs = new MutableObservable(false);

    @Setter
    private @Nullable StringValueResolver embeddedValueResolver;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        final Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
        registerRedisPubSubs(targetClass, bean);
        registerRedisPubSubHandlers(targetClass, bean);
        return bean;
    }

    private void registerRedisPubSubs(Class<?> targetClass, Object bean) {
        Method[] methodList = MethodUtils.getMethodsWithAnnotation(targetClass, RedisPubSub.class);
        if (ArrayUtils.isNotEmpty(methodList)) {
            for (final Method method : methodList) {
                RedisPubSub pubsub = method.getAnnotation(RedisPubSub.class);
                String channel = pubsub.value();
                if (embeddedValueResolver != null) {
                    channel = embeddedValueResolver.resolveStringValue(channel);
                }
                MutableObserver ob = getMutableObserver(bean, method, pubsub.primary());
                if (pubsub.repeatable()) {
                    repeableObs.addObserver(channel, ob);
                } else {
                    unrepeableObs.addObserver(channel, ob);
                }
                if (log.isInfoEnabled()) {
                    log.info("Registered pubsub method: {}#{}", targetClass.getName(), method.toString());
                }
            }
        }
    }

    private void registerRedisPubSubHandlers(Class<?> targetClass, Object bean) {
        if (bean instanceof RedisPubSubHandler) {
            RedisPubSubHandler handler = (RedisPubSubHandler) bean;
            String channel = handler.getChannel();
            MutableObserver ob = getMutableObserver(handler);
            if (handler.isRepeatable()) {
                repeableObs.addObserver(channel, ob);
            } else {
                unrepeableObs.addObserver(channel, ob);
            }
            if (log.isInfoEnabled()) {
                log.info("Registered pubsub handler: {}", targetClass.getName());
            }
        }
    }

    private MutableObserver getMutableObserver(final Object bean, final Method method, final boolean primary) {
        return new MutableObserver() {

            @Override
            public void update(Observable o, Object arg) {
                try {
                    MethodUtils.invokeMethod(bean, method.getName(), (Object[]) arg);
                } catch (Exception e) {
                    throw new IllegalStateException(e.getMessage(), e);
                }
            }

            @Override
            public boolean isPrimary() {
                return primary;
            }
        };
    }

    private MutableObserver getMutableObserver(final RedisPubSubHandler handler) {
        return new MutableObserver() {

            @Override
            public void update(Observable o, Object arg) {
                try {
                    Object[] args = (Object[]) arg;
                    handler.onMessage((String) args[0], args[1]);
                } catch (Exception e) {
                    throw new IllegalStateException(e.getMessage(), e);
                }
            }

            @Override
            public boolean isPrimary() {
                return handler.isPrimary();
            }
        };
    }

    @EventListener(RedisMessageEvent.class)
    public void handleRedisMessageEvent(RedisMessageEvent event) {
        Object[] args = new Object[]{event.getChannel(), event.getMessage()};
        boolean result = repeableObs.notifyObservers(event.getChannel(), args);
        if (!result) {
            repeableObs.notifyObservers(event.getChannel(), MatchMode.WILDCARD, args);
        }

        result = unrepeableObs.notifyObservers(event.getChannel(), args);
        if (!result) {
            unrepeableObs.notifyObservers(event.getChannel(), MatchMode.WILDCARD, args);
        }
    }
}