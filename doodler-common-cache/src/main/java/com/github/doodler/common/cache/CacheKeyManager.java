package com.github.doodler.common.cache;

import java.util.Set;
import com.github.doodler.common.utils.MatchMode;

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