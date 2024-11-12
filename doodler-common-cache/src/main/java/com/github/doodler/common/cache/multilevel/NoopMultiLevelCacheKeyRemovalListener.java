package com.github.doodler.common.cache.multilevel;

/**
 * 
 * @Description: NoopMultiLevelCacheKeyRemovalListener
 * @Author: Fred Feng
 * @Date: 31/01/2023
 * @Version 1.0.0
 */
public class NoopMultiLevelCacheKeyRemovalListener implements MultiLevelCacheKeyRemovalListener{

	@Override
	public void onRemovalLocalCacheKey(String cacheName, Object cacheKey, Object eldestValue) {
	}

	@Override
	public void onRemovalRemoteCacheKey(String cacheName, Object cacheKey, Object eldestValue) {
	}

}
