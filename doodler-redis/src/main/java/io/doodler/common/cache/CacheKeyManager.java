package io.doodler.common.cache;

import io.doodler.common.utils.MatchMode;
import java.util.Set;

/**
 * @Description: CacheKeyManager
 * @Author: Fred Feng
 * @Date: 14/04/2023
 * @Version 1.0.0
 */
public interface CacheKeyManager {

    Set<Object> getCacheKeys(String cacheName);

    Set<String> getCacheKeys(String cacheName, String cacheKeyPattern, MatchMode matchMode);
}