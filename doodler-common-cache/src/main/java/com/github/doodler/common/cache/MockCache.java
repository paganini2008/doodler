package com.github.doodler.common.cache;

import org.springframework.cache.Cache;

/**
 * @Description: MockCache
 * @Author: Fred Feng
 * @Date: 31/01/2023
 * @Version 1.0.0
 */
public interface MockCache extends Cache {

    void justEvict(Object cacheKey);

    void justClear();
}