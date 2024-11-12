package com.github.doodler.common.cache.statistics;

import org.apache.commons.lang3.StringUtils;
import com.github.doodler.common.cache.filter.CacheMethodFilter;
import com.github.doodler.common.cache.spec.CacheSpecifications;

/**
 * @Description: CacheStatisticsFilter
 * @Author: Fred Feng
 * @Date: 31/01/2023
 * @Version 1.0.0
 */
public class CacheStatisticsFilter implements CacheMethodFilter {

	private final String applicationName;
    private final CacheStatisticsService statisticsService;
    private final CacheSpecifications cacheSpecifications;

    public CacheStatisticsFilter(String applicationName, CacheStatisticsService statisticsService, CacheSpecifications cacheSpecifications) {
        this.applicationName = applicationName;
    	this.statisticsService = statisticsService;
        this.cacheSpecifications = cacheSpecifications;
    }

    @Override
    public void onGet(String cacheName, Object cacheKey, Object value) {
        String applicationName = getApplicationName(cacheName);
        if (value != null) {
            statisticsService.update("cache", cacheName, System.currentTimeMillis(), item -> {
                item.getSample().hits.increment();
            });
            if (StringUtils.isNotBlank(applicationName)) {
                statisticsService.update("all", applicationName, System.currentTimeMillis(), item -> {
                    item.getSample().hits.increment();
                });
            }
        }
        statisticsService.update("cache", cacheName, System.currentTimeMillis(), item -> {
            item.getSample().gets.increment();
        });
        if (StringUtils.isNotBlank(applicationName)) {
            statisticsService.update("all", applicationName, System.currentTimeMillis(), item -> {
                item.getSample().gets.increment();
            });
        }
    }

    @Override
    public void onEvict(String cacheName, Object key) {
        statisticsService.update("cache", cacheName, System.currentTimeMillis(), item -> {
            item.getSample().evicts.increment();
        });
        String applicationName = getApplicationName(cacheName);
        if (StringUtils.isNotBlank(applicationName)) {
            statisticsService.update("all", applicationName, System.currentTimeMillis(), item -> {
                item.getSample().evicts.increment();
            });
        }
    }

    @Override
    public void onPut(String cacheName, Object cacheKey) {
        statisticsService.update("cache", cacheName, System.currentTimeMillis(), item -> {
            item.getSample().puts.increment();
        });
        String applicationName = getApplicationName(cacheName);
        if (StringUtils.isNotBlank(applicationName)) {
            statisticsService.update("all", applicationName, System.currentTimeMillis(), item -> {
                item.getSample().puts.increment();
            });
        }
    }

    private String getApplicationName(String cacheName) {
        if (cacheSpecifications.isOwner(cacheName)) {
            return applicationName;
        }
        return cacheSpecifications.getSharedApplicationName(cacheName);
    }
}