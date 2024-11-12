package com.github.doodler.common.amqp;

import com.github.doodler.common.amqp.RetryCache.CachedObject;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.googlecode.concurrentlinkedhashmap.EvictionListener;
import com.googlecode.concurrentlinkedhashmap.Weighers;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentMap;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: LruRetryCache
 * @Author: Fred Feng
 * @Date: 15/01/2023
 * @Version 1.0.0
 */
@Slf4j
public class LruRetryCache implements EvictionListener<String, CachedObject>, RetryCache {

    private final ConcurrentMap<String, CachedObject> cache;

    public LruRetryCache() {
        this(10000);
    }

    public LruRetryCache(int maxSize) {
        this.cache = new ConcurrentLinkedHashMap.Builder<String, CachedObject>().maximumWeightedCapacity(maxSize).weigher(
                Weighers.singleton()).listener(this).build();
    }

    public boolean putObject(String id, CachedObject cachedObject) {
        return cache.put(id, cachedObject) == null;
    }

    public CachedObject getObject(String id) {
        return cache.get(id);
    }

    public CachedObject removeObject(String id) {
        return cache.remove(id);
    }

    public Collection<String> remainingIds() {
        return Collections.unmodifiableSet(cache.keySet());
    }

    public int size() {
        return cache.size();
    }

    @Override
    public void onEviction(String key, CachedObject eldestValue) {
        if (log.isWarnEnabled()) {
            log.warn("Discard object: " + eldestValue.toString());
        }
    }
}