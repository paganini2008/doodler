package com.github.doodler.common.cache.feign;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.serializer.RedisSerializer;
import com.github.doodler.common.annotations.Serializer;
import com.github.doodler.common.annotations.Ttl;
import com.github.doodler.common.cache.CacheLifeCycleExtension;
import com.github.doodler.common.cache.spec.CacheSpecifications;
import com.github.doodler.common.feign.InitializingRestClientBean;
import com.github.doodler.common.feign.RestClient;
import com.github.doodler.common.redis.RedisSerializerUtils;
import lombok.RequiredArgsConstructor;

/**
 * @Description: CacheableInitializingRestClientBean
 * @Author: Fred Feng
 * @Date: 01/02/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class CacheableInitializingRestClientBean implements InitializingRestClientBean {

    private static final String THIRD_PARTY_SERVICE_ID = "open-api";
    private final CacheSpecifications cacheSpecifications;
    private final CacheLifeCycleExtension cacheLifeCycleExtension;

    @Override
    public void initialize(Object proxyBean, Class<?> apiInterfaceClass, String beanName) {
        RestClient restClient = apiInterfaceClass.getAnnotation(RestClient.class);
        String serviceId = restClient.serviceId();
        if (StringUtils.isBlank(serviceId)) {
            serviceId = THIRD_PARTY_SERVICE_ID;
        }
        CacheConfig cacheConfig = apiInterfaceClass.getAnnotation(CacheConfig.class);
        if (cacheConfig != null && ArrayUtils.isNotEmpty(cacheConfig.cacheNames())) {
            cacheSpecifications.addCacheNames(cacheConfig.cacheNames(), serviceId);
        }
        List<Method> annotatedMethods = MethodUtils.getMethodsListWithAnnotation(apiInterfaceClass, Cacheable.class);
        if (CollectionUtils.isNotEmpty(annotatedMethods)) {
            for (Method method : annotatedMethods) {
                Set<String> cacheNames = new HashSet<>();
                Cacheable cacheable = method.getAnnotation(Cacheable.class);
                if (ArrayUtils.isNotEmpty(cacheable.value())) {
                    cacheSpecifications.addCacheNames(cacheable.value(), serviceId);
                    cacheNames.addAll(Arrays.asList(cacheable.value()));
                } else if (ArrayUtils.isNotEmpty(cacheable.cacheNames())) {
                    cacheSpecifications.addCacheNames(cacheable.cacheNames(), serviceId);
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
            }
        }
    }
}