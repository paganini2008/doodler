package io.doodler.cache.redis;

import io.doodler.cache.CacheHitStatistics;
import io.doodler.cache.CacheHitStatisticsCollector;
import io.doodler.common.ApiResult;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * @Description: RedisCacheHitEndpoint
 * @Author: Fred Feng
 * @Date: 14/04/2023
 * @Version 1.0.0
 */
@ConditionalOnProperty(name = "spring.cache.extension.type", havingValue = "redis", matchIfMissing = true)
@Endpoint(id = "redisCacheHits")
@Component
public class RedisCacheHitEndpoint {

	@Autowired
	private CacheHitStatisticsCollector cacheHitStatisticsCollector;

	@ReadOperation
	public ApiResult<Map<String, CacheHitStatistics>> statistics() {
		return ApiResult.ok(cacheHitStatisticsCollector.statistics());
	}
}