package com.github.doodler.common.amqp;

import java.util.Collection;

/**
 * @Description: RetryCache
 * @Author: Fred Feng
 * @Date: 15/01/2023
 * @Version 1.0.0
 */
public interface RetryCache {

    boolean putObject(String id, CachedObject cachedObject);

    CachedObject getObject(String id);

    CachedObject removeObject(String id);

    Collection<String> remainingIds();

    int size();

    default boolean isEmpty() {
        return size() == 0;
    }

    /**
     * @Description: CachedObject
     * @Author: Fred Feng
     * @Date: 15/01/2023
     * @Version 1.0.0
     */
    interface CachedObject {

        CachedObject setExchange(String exchange);

        String getExchange();

        CachedObject setRoutingKey(String routingKey);

        String getRoutingKey();

        CachedObject setRetryAt(long retryAt);

        long getRetryAt();

        Object getObject();
    }
}