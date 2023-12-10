package io.doodler.common.feign.actuator;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;

import io.doodler.common.utils.LangUtils;
import io.doodler.common.utils.MapUtils;
import io.doodler.common.utils.statistics.Sampler;

import io.doodler.common.feign.statistics.HttpSample;
import io.doodler.common.feign.statistics.RestClientStatisticsService;
import lombok.RequiredArgsConstructor;

/**
 * @Description: RestClientStatisticsHealthIndicator
 * @Author: Fred Feng
 * @Date: 15/10/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class RestClientStatisticsHealthIndicator extends AbstractHealthIndicator {

	private final RestClientStatisticsService statisticsService;

	@Override
	protected void doHealthCheck(Builder builder) throws Exception {
		builder.up();
		Map<String, Object> map = null;
		Map<String, Sampler<HttpSample>> samplers = statisticsService.rank("action",
				(a, b) -> LangUtils.compareTo(b.getSample().getTotalExecutionCount(),
						a.getSample().getTotalExecutionCount()), 100);
		if (MapUtils.isNotEmpty(samplers)) {
			map = samplers.entrySet().stream().collect(LinkedHashMap::new,
					(m, e) -> m.put(e.getKey(), e.getValue().getSample().getTotalExecutionCount()),
					LinkedHashMap::putAll);
			builder.withDetail("totalExecutionCount", map);
		}

		samplers = statisticsService.rank("action",
				(a, b) -> LangUtils.compareTo(b.getSample().getSuccessPercent(), a.getSample().getSuccessPercent()), 100);
		if (MapUtils.isNotEmpty(samplers)) {
			map = samplers.entrySet().stream().collect(LinkedHashMap::new,
					(m, e) -> m.put(e.getKey(), e.getValue().getSample().getSuccessPercent()),
					LinkedHashMap::putAll);
			builder.withDetail("successPercent", map);
		}

		samplers = statisticsService.rank("action",
				(a, b) -> LangUtils.compareTo(b.getSample().getAverageExecutionTime(),
						a.getSample().getAverageExecutionTime()), 100);
		if (MapUtils.isNotEmpty(samplers)) {
			map = samplers.entrySet().stream().collect(LinkedHashMap::new,
					(m, e) -> m.put(e.getKey(), e.getValue().getSample().getAverageExecutionTime()),
					LinkedHashMap::putAll);
			builder.withDetail("averageExecutionTime", map);
		}

		samplers = statisticsService.rank("action",
				(a, b) -> LangUtils.compareTo(b.getSample().getTps(), a.getSample().getTps()), 100);
		if (MapUtils.isNotEmpty(samplers)) {
			map = samplers.entrySet().stream().collect(LinkedHashMap::new,
					(m, e) -> m.put(e.getKey(), e.getValue().getSample().getTps()),
					LinkedHashMap::putAll);
			builder.withDetail("tps", map);
		}

		samplers = statisticsService.rank("action",
				(a, b) -> LangUtils.compareTo(b.getSample().getConcurrentCount(), a.getSample().getConcurrentCount()), 100);
		if (MapUtils.isNotEmpty(samplers)) {
			map = samplers.entrySet().stream().collect(LinkedHashMap::new,
					(m, e) -> m.put(e.getKey(), e.getValue().getSample().getConcurrentCount()),
					LinkedHashMap::putAll);
			builder.withDetail("concurrentCount", map);
		}
		builder.build();
	}
}