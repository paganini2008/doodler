package com.github.doodler.common.cache;

import java.util.Observable;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

/**
 * @Description: CacheLifeCycleExtension
 * @Author: Fred Feng
 * @Date: 14/04/2023
 * @Version 1.0.0
 */
public class CacheLifeCycleExtension {

    private final CacheManager cacheManager;
    private final Observable contextRefreshedObs = new Observable();
    private final Observable contextClosedObs = new Observable();

    @Lazy
    public CacheLifeCycleExtension(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void waitForCleanCacheOnContextRefreshed(String cacheName) {
    	contextRefreshedObs.addObserver((ob, arg) -> {
            cleanCache(cacheName);
        });
    }

    public void waitForCleanCacheOnContextClosed(String cacheName) {
    	contextClosedObs.addObserver((ob, arg) -> {
            cleanCache(cacheName);
        });
    }

    private void cleanCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }

    @EventListener(ContextRefreshedEvent.class)
    public void doDeleteCacheOnStarted() {
    	contextRefreshedObs.notifyObservers();
    }

    @EventListener(ContextClosedEvent.class)
    public void doDeleteCacheOnExit() {
    	contextClosedObs.notifyObservers();
    }
}