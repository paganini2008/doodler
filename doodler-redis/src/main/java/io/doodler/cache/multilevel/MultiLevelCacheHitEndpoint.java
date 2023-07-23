package io.doodler.cache.multilevel;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import io.doodler.cache.CacheHitStatisticsCollector;
import io.doodler.common.ApiResult;

/**
 * @Description: MultiLevelCacheHitEndpoint
 * @Author: Fred Feng
 * @Date: 26/01/2023
 * @Version 1.0.0
 */
@ConditionalOnProperty(name = "spring.cache.extension.type", havingValue = "multilevel")
@Endpoint(id = "multilevelCacheHits")
@Component
public class MultiLevelCacheHitEndpoint {

	@Autowired
	@Qualifier("localCacheHitStatisticsCollector")
	public CacheHitStatisticsCollector localCacheHitStatisticsCollector;

	@Autowired
	@Qualifier("remoteCacheHitStatisticsCollector")
	private CacheHitStatisticsCollector remoteCacheHitStatisticsCollector;

	@ReadOperation
	public ApiResult<Map<String, Object>> statistics() {
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("local", localCacheHitStatisticsCollector.statistics());
		resultMap.put("remote", remoteCacheHitStatisticsCollector.statistics());
		return ApiResult.ok(resultMap);
	}
}