package com.github.doodler.common.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

/**
 * @Description: CacheChangeEvent
 * @Author: Fred Feng
 * @Date: 30/01/2023
 * @Version 1.0.0
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CacheChangeEvent {

    private String applicationName;
    private String instanceId;
    private String cacheName;
    private @Nullable Object cacheKey;
    private CacheChangeType eventType;
}