package com.github.doodler.common.cache.redis;

import static com.github.doodler.common.cache.CacheConstants.REDIS_CACHE_NAME_PREFIX_PATTERN;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.util.StringUtils;
import com.github.doodler.common.cache.CacheKeyRemovalListener;

/**
 * @Description: RedisCacheKeyRemovalListenerContainer
 * @Author: Fred Feng
 * @Date: 31/01/2023
 * @Version 1.0.0
 */
public class RedisCacheKeyRemovalListenerContainer {

    @Value("${spring.application.name}")
    private String applicationName;

    private final List<CacheKeyRemovalListener> cacheKeyRemovalListeners = new CopyOnWriteArrayList<>();

    public void addCacheKeyRemovalListener(CacheKeyRemovalListener cacheKeyRemovalListener) {
        if (cacheKeyRemovalListener != null) {
            cacheKeyRemovalListeners.add(cacheKeyRemovalListener);
        }
    }

    @EventListener({RedisKeyExpiredEvent.class})
    public void handleExpiredCacheKey(RedisKeyExpiredEvent<?> event) {
        String expiredKey = new String(event.getSource());
        String keyPrefix = String.format(REDIS_CACHE_NAME_PREFIX_PATTERN, applicationName);
        if (!expiredKey.startsWith(keyPrefix)) {
            return;
        }
        String[] args = StringUtils.delimitedListToStringArray(expiredKey, "::");
        if (args.length == 2) {
            String cacheName = args[0].replace(keyPrefix, "");
            String cacheKey = args[1];
            handleExpiredCacheKey(cacheName, cacheKey);
        }
    }

    @EventListener({ContextRefreshedEvent.class})
    public void addCacheKeyRemovalListeners(ContextRefreshedEvent event) {
        Map<String, CacheKeyRemovalListener> beans = event.getApplicationContext().getBeansOfType(
                CacheKeyRemovalListener.class);
        if (MapUtils.isNotEmpty(beans)) {
            cacheKeyRemovalListeners.addAll(beans.values());
        }
    }

    private void handleExpiredCacheKey(String cacheName, String cacheKey) {
        cacheKeyRemovalListeners.forEach(cacheKeyRemovalListener -> {
            cacheKeyRemovalListener.onRemoval(cacheName, cacheKey, null);
        });
    }
}