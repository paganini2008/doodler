package com.github.doodler.common.cache.spec;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.Assert;
import com.github.doodler.common.annotations.TtlUnit;

/**
 * @Description: CacheSpecifications
 * @Author: Fred Feng
 * @Date: 30/01/2023
 * @Version 1.0.0
 */
public class CacheSpecifications {

    private final Map<String, String> cacheOwners = new ConcurrentHashMap<>();
    private final Map<String, String> cacheSharers = new ConcurrentHashMap<>();
    private final Map<String, TtlSpec> cacheTtlSpecs = new ConcurrentHashMap<>();
    private final Map<String, RedisSerializer<Object>> cacheValueSerializers = new ConcurrentHashMap<>();

    @Value("${spring.application.name}")
    private String applicationName;

    public boolean isOwner(String cacheName) {
        return cacheOwners.containsKey(cacheName);
    }

    public boolean isSharer(String cacheName) {
        return cacheSharers.containsKey(cacheName);
    }

    public String getSharedApplicationName(String cacheName) {
        return cacheSharers.get(cacheName);
    }

    public void addCacheName(String cacheName) {
        addCacheName(cacheName, applicationName);
    }

    public void addCacheName(String cacheName, String applicationName) {
        Assert.hasText(applicationName, "Application name must be required");
        if (applicationName.equals(this.applicationName)) {
            cacheOwners.put(cacheName, applicationName);
        } else {
            cacheSharers.put(cacheName, applicationName);
        }
    }

    public void addCacheNames(String[] cacheNames) {
        addCacheNames(cacheNames, applicationName);
    }

    public void addCacheNames(String[] cacheNames, String applicationName) {
        Assert.hasText(applicationName, "Application name must be required");
        if (ArrayUtils.isNotEmpty(cacheNames)) {
            for (String cacheName : cacheNames) {
                if (applicationName.equals(this.applicationName)) {
                    cacheOwners.put(cacheName, applicationName);
                } else {
                    cacheSharers.put(cacheName, applicationName);
                }
            }
        }
    }

    public Set<String> getCacheNames() {
        Set<String> cacheNames = new HashSet<>();
        cacheNames.addAll(cacheOwners.keySet());
        cacheNames.addAll(cacheSharers.keySet());
        return cacheNames;
    }

    public TtlSpec getTtlSpec(String cacheName) {
        return cacheTtlSpecs.get(cacheName);
    }

    public void addTtlSpec(long ttl, TtlUnit ttlUnit, String... cacheNames) {
        if (ArrayUtils.isNotEmpty(cacheNames)) {
            for (String cacheName : cacheNames) {
                cacheTtlSpecs.put(cacheName, new TtlSpec(ttl, ttlUnit));
            }
        }
    }

    public int sizeOfCacheNames() {
        return cacheOwners.size() + cacheSharers.size();
    }
    
    public RedisSerializer<Object> getValueSerializer(String cacheName){
    	return cacheValueSerializers.get(cacheName);
    }

    public void addValueSerializer(RedisSerializer<Object> redisSerializer, String... cacheNames) {
        if (ArrayUtils.isNotEmpty(cacheNames)) {
            for (String cacheName : cacheNames) {
                cacheValueSerializers.put(cacheName, redisSerializer);
            }
        }
    }
}