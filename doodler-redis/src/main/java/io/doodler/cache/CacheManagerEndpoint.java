package io.doodler.cache;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.DeleteOperation;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import io.doodler.common.ApiResult;
import io.doodler.common.utils.MatchMode;

/**
 * @Description: CacheManagerEndpoint
 * @Author: Fred Feng
 * @Date: 20/04/2023
 * @Version 1.0.0
 */
@Endpoint(id = "cacheman")
@Component
public class CacheManagerEndpoint {

	@Autowired
	private CacheManager cacheManager;

	@ReadOperation
	public ApiResult<Map<String, Collection<Object>>> getCacheNameAndKeys() {
		Collection<String> cacheNames = cacheManager.getCacheNames();
		if (CollectionUtils.isEmpty(cacheNames)) {
			return ApiResult.ok(Collections.emptyMap());
		}
		Map<String, Collection<Object>> results = new HashMap<>();
		cacheNames.forEach(cacheName -> {
			Cache cache = cacheManager.getCache(cacheName);
			if (cache != null && cacheManager instanceof CacheKeyManager) {
				Set<Object> cacheKeys = ((CacheKeyManager) cacheManager).getCacheKeys(cacheName);
				if (CollectionUtils.isNotEmpty(cacheKeys)) {
					results.put(cacheName, cacheKeys);
				}
			}
		});
		return ApiResult.ok(results);
	}

	@DeleteOperation
	public ApiResult<String> evictCache(@Selector String cacheName, @Selector String cacheKeyPattern,
	                                    @Selector String matchMode) {
		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null && cacheManager instanceof CacheKeyManager) {
			Set<String> cacheKeys = ((CacheKeyManager) cacheManager).getCacheKeys(cacheName, cacheKeyPattern,
					MatchMode.getBy(matchMode));
			if (CollectionUtils.isNotEmpty(cacheKeys)) {
				for (String cacheKey : cacheKeys) {
					cache.evict(cacheKey);
				}
			}
		}
		return ApiResult.ok("Evict operation is ok");
	}

	@DeleteOperation
	public ApiResult<String> evictCache(@Selector String cacheName) {
		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {
			cache.clear();
		}
		return ApiResult.ok("Evict operation is ok");
	}

	@DeleteOperation
	public ApiResult<String> evictCache(@Selector String cacheName, @Selector Object cacheKey) {
		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {
			cache.evict(cacheKey);
		}
		return ApiResult.ok("Evict operation is ok");
	}
}