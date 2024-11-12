package com.github.doodler.common.cache.spec;

import com.github.doodler.common.annotations.Serializer;
import com.github.doodler.common.annotations.Ttl;
import com.github.doodler.common.cache.CacheLifeCycleExtension;
import com.github.doodler.common.redis.RedisSerializerUtils;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @Description: CacheSpecificationsBeanPostProcessor
 * @Author: Fred Feng
 * @Date: 30/01/2023
 * @Version 1.0.0
 */
public class CacheSpecificationsBeanPostProcessor implements BeanPostProcessor {

    @Autowired
    private CacheSpecifications cacheSpecifications;

    @Autowired
    private CacheLifeCycleExtension cacheLifeCycleExtension;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        final Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
        CacheConfig cacheConfig = targetClass.getAnnotation(CacheConfig.class);
        if (cacheConfig != null && ArrayUtils.isNotEmpty(cacheConfig.cacheNames())) {
            cacheSpecifications.addCacheNames(cacheConfig.cacheNames());
        }

        List<Method> annotatedMethods = MethodUtils.getMethodsListWithAnnotation(targetClass, Cacheable.class);
        if (CollectionUtils.isNotEmpty(annotatedMethods)) {
            annotatedMethods.forEach(method -> {
                Set<String> cacheNames = new HashSet<>();
                Cacheable cacheable = method.getAnnotation(Cacheable.class);
                if (ArrayUtils.isNotEmpty(cacheable.value())) {
                    cacheSpecifications.addCacheNames(cacheable.value());
                    cacheNames.addAll(Arrays.asList(cacheable.value()));
                } else if (ArrayUtils.isNotEmpty(cacheable.cacheNames())) {
                    cacheSpecifications.addCacheNames(cacheable.cacheNames());
                    cacheNames.addAll(Arrays.asList(cacheable.cacheNames()));
                }

                Serializer serializer = method.getAnnotation(Serializer.class);
                if (serializer != null) {
                    RedisSerializer<Object> redisSerializer = RedisSerializerUtils.chooseRedisSerializer(
                            serializer.value());
                    cacheSpecifications.addValueSerializer(redisSerializer, cacheNames.toArray(new String[0]));
                }

                Ttl ttl = method.getAnnotation(Ttl.class);
                if (ttl != null) {
                    cacheSpecifications.addTtlSpec(ttl.value(), ttl.ttlUnit(), cacheNames.toArray(new String[0]));

                    if (ttl.evictOnContextRefreshed()) {
                        cacheNames.forEach(cacheName -> {
                            cacheLifeCycleExtension.waitForCleanCacheOnContextRefreshed(cacheName);
                        });
                    }
                    if (ttl.evictOnContextClosed()) {
                        cacheNames.forEach(cacheName -> {
                            cacheLifeCycleExtension.waitForCleanCacheOnContextClosed(cacheName);
                        });
                    }
                }
            });
        }
        return bean;
    }
}