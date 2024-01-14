package com.github.doodler.common.cache;

/**
 * @Description: CacheConstants
 * @Author: Fred Feng
 * @Date: 26/03/2021
 * @Version 1.0.0
 */
public interface CacheConstants {

    long DEFAULT_CAFFEINE_TTL_IN_SEC = 300L;

    long DEFAULT_REDIS_TTL_IN_SEC = 1L * 24 * 60 * 60;

    String REDIS_CACHE_NAME_PREFIX = "crypto:";

    String REDIS_CACHE_NAME_PREFIX_PATTERN = "crypto:%s:";
    
    String REDIS_CACHE_NAME_PATTERN = "crypto:%s:%s";

    String REDIS_CACHE_NAME_DELIMITER = "::";

    String DEFAULT_CAFFEINE_CACHE_SPEC = "initialCapacity=100,maximumSize=1000,expireAfterWrite=%ds";

    String DEFAULT_CACHE_KEY_PATTERN = "%s.%s(%s)";

    String PUBSUB_CHANNEL_CACHE_CHANGE_EVENT = "CACHE_CHANGE_EVENT_BOARDCAST";
    
    int DEFAULT_CACHE_SYNC_INTERVAL = 60;
}