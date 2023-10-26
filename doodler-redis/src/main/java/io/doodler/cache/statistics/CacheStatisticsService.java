package io.doodler.cache.statistics;

import io.doodler.common.utils.TimeWindowUnit;
import io.doodler.common.utils.statistics.SampleCollector;
import io.doodler.common.utils.statistics.Sampler;
import io.doodler.common.utils.statistics.StatisticsService;
import io.doodler.common.utils.statistics.TimeWindowSampleCollector;
import io.doodler.common.utils.statistics.UserSampler;

/**
 * @Description: CacheStatisticsService
 * @Author: Fred Feng
 * @Date: 25/09/2023
 * @Version 1.0.0
 */
public class CacheStatisticsService extends StatisticsService<CacheSample> {

	@Override
	protected SampleCollector<CacheSample> getSampleCollector(long timestampMillis) {
		return new TimeWindowSampleCollector<>(5, TimeWindowUnit.MINUTES,
				() -> getEmptySampler(timestampMillis));
	}

	@Override
	protected Sampler<CacheSample> getEmptySampler(long timestampMillis) {
		return new UserSampler<CacheSample>(timestampMillis, new CacheSample());
	}
}