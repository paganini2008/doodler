package com.github.doodler.common.cache.redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import com.github.doodler.common.enums.AppName;
import com.github.doodler.common.utils.MapUtils;
import com.github.doodler.common.utils.SimpleTimer;

/**
 * @Description: RedisCacheNameFinder
 * @Author: Fred Feng
 * @Date: 26/09/2023
 * @Version 1.0.0
 */
public class RedisCacheNameFinder extends SimpleTimer {

    private final RedisCacheLoader redisCacheLoader;

    public RedisCacheNameFinder(long period, TimeUnit timeUnit, RedisCacheLoader redisCacheLoader) {
        super(period, timeUnit);
        this.redisCacheLoader = redisCacheLoader;
    }

    private final Map<String, String> cacheNameAndApplicationNames = new HashMap<>();
    private final Map<String, List<String>> applicationNameAndCacheNames = new HashMap<>();

    public String getApplicationName(String cacheName) {
        return cacheNameAndApplicationNames.get(cacheName);
    }

    public List<String> getCacheNames(String applicationName) {
        return applicationNameAndCacheNames.get(applicationName);
    }

    @Override
    public boolean change() throws Exception {
        Map<String, String> cacheNames = new HashMap<>();
        for (AppName appName : AppName.values()) {
            String applicationName = appName.getFullName();
            Set<String> names = redisCacheLoader.getCacheNames(applicationName, null);
            names.forEach(name -> {
                cacheNames.put(name, applicationName);
            });
        }
        cacheNameAndApplicationNames.clear();
        cacheNameAndApplicationNames.putAll(cacheNames);

        Map<String, List<String>> map = new HashMap<>();
        for (Map.Entry<String, String> entry : cacheNames.entrySet()) {
            List<String> list = MapUtils.getOrCreate(map, entry.getValue(), ArrayList::new);
            list.add(entry.getKey());
        }
        applicationNameAndCacheNames.clear();
        applicationNameAndCacheNames.putAll(map);
        return true;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReadyEvent() {
        run();
    }
}
