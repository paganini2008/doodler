package com.github.doodler.common.cache.statistics;

import com.github.doodler.common.utils.TimeWindowUnit;
import com.github.doodler.common.utils.statistics.SampleCollector;
import com.github.doodler.common.utils.statistics.Sampler;
import com.github.doodler.common.utils.statistics.StatisticsService;
import com.github.doodler.common.utils.statistics.TimeWindowSampleCollector;
import com.github.doodler.common.utils.statistics.UserSampler;

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