package com.github.doodler.common.cache.multilevel;

import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;

/**
 * @Description: MultiLevelCache
 * @Author: Fred Feng
 * @Date: 26/01/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class MultiLevelCache implements Cache {

    private final String name;
    private final Cache localCache;
    private final Cache remoteCache;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

    @Override
    public ValueWrapper get(Object key) {
        ValueWrapper valueWrapper = localCache.get(key);
        if (valueWrapper == null) {
            valueWrapper = remoteCache.get(key);
            if (valueWrapper != null) {
                localCache.put(key, valueWrapper.get());
            }
            valueWrapper = localCache.get(key);
        }
        return valueWrapper;
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        T value = localCache.get(key, type);
        if (value == null) {
            value = remoteCache.get(key, type);
            if (value != null) {
                localCache.put(key, value);
            }
            value = localCache.get(key, type);
        }
        return value;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        T value = localCache.get(key, valueLoader);
        if (value == null) {
            value = remoteCache.get(key, valueLoader);
            if (value != null) {
                localCache.put(key, value);
            }
            value = localCache.get(key, valueLoader);
        }
        return value;
    }

    @Override
    public void put(Object key, Object value) {
        localCache.put(key, value);
        remoteCache.put(key, value);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        localCache.putIfAbsent(key, value);
        return remoteCache.putIfAbsent(key, value);
    }

    @Override
    public void evict(Object key) {
        localCache.evict(key);
        remoteCache.evict(key);
    }

    @Override
    public void clear() {
        localCache.clear();
        remoteCache.clear();
    }
}