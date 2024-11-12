package com.github.doodler.common.cache.redis;

import java.util.HashSet;
import java.util.Set;
import java.util.function.ToDoubleFunction;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import com.github.doodler.common.cache.spec.CacheSpecifications;
import com.github.doodler.common.cache.statistics.CacheStatisticsService;
import com.github.doodler.common.context.MetricsCollector;
import cn.hutool.core.net.NetUtil;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: RedisCacheStatisticsMetricsCollector
 * @Author: Fred Feng
 * @Date: 24/09/2023
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class RedisCacheStatisticsMetricsCollector implements MetricsCollector {

	private final MeterRegistry registry;
	private final CacheStatisticsService statisticsService;
	private final CacheSpecifications cacheSpecifications;

	@Value("${spring.application.name}")
	private String applicationName;

	@Value("${spring.profiles.active}")
	private String env;

	@Value("${server.port}")
	private int port;

	private final String localHost = NetUtil.getLocalhostStr();

	@Override
	public void refreshMetrics() throws Exception {
		final Set<String> applicationNames = new HashSet<>();
		Set<String> cacheNames = cacheSpecifications.getCacheNames();
		cacheNames.forEach(cacheName -> {
			if (cacheSpecifications.isOwner(cacheName)) {
				applicationNames.add(this.applicationName);
			} else if (cacheSpecifications.isSharer(cacheName)) {
				String applicationName = cacheSpecifications.getSharedApplicationName(cacheName);
				if (StringUtils.isNotBlank(applicationName)) {
					applicationNames.add(applicationName);
				}
			}
		});
		applicationNames.forEach(from -> {
			
			createGauge(statisticsService, "redis.cache.hit.percent", "Redis cache hit percent", from, statisticsService -> {
				return statisticsService.sampler("all", from).getSample().getHitPercent();
			});
			
			createGauge(statisticsService, "redis.cache.put.percent", "Redis cache put percent", from, statisticsService -> {
				return statisticsService.sampler("all", from).getSample().getPutPercent();
			});
			
		});
	}

	private void createGauge(CacheStatisticsService ref, String metric, String help, String from,
	                         ToDoubleFunction<CacheStatisticsService> measure) {
		Gauge.builder(metric, ref, measure)
				.description(help)
				.tag("env", env)
				.tag("instance", localHost + ":" + port)
				.tag("from", from)
				.register(this.registry);
	}

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() throws Exception {
        refreshMetrics();
        if (log.isTraceEnabled()) {
            log.trace("Start to collect metrics and push to MeterRegistry ...");
        }
    }
}