package com.github.doodler.common.quartz.scheduler;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Value;
import com.github.doodler.common.quartz.executor.JobSignature;
import com.github.doodler.common.utils.MapUtils;
import cn.hutool.core.net.NetUtil;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import lombok.RequiredArgsConstructor;

/**
 * @Description: CounterMetricsJobSchedulingListener
 * @Author: Fred Feng
 * @Date: 20/11/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class CounterMetricsJobSchedulingListener implements JobSchedulingListener {

	private final MeterRegistry meterRegistry;

	@Value("${spring.profiles.active}")
	private String env;

	@Value("${server.port}")
	private int port;

	private final String localHost = NetUtil.getLocalhostStr();

	private final Map<String, Counter> completedCounters = new ConcurrentHashMap<>();
	private final Map<String, Counter> rungingCounters = new ConcurrentHashMap<>();
	private final Map<String, Counter> errorCounters = new ConcurrentHashMap<>();

	@Override
	public void beforeScheduling(long startTime, JobSignature jobSignature) {
		MapUtils.getOrCreate(rungingCounters, jobSignature.getJobGroup(),
				() -> createCounter("quartz.job.running.count", jobSignature)).increment();
	}

	@Override
	public void afterScheduling(long startTime, JobSignature jobSignature, Throwable reason) {
		MapUtils.getOrCreate(rungingCounters, jobSignature.getJobGroup(),
				() -> createCounter("quartz.job.running.count", jobSignature)).increment(-1);
		MapUtils.getOrCreate(completedCounters, jobSignature.getJobGroup(),
				() -> createCounter("quartz.job.completed.count", jobSignature)).increment();
		if (reason != null) {
			MapUtils.getOrCreate(errorCounters, jobSignature.getJobGroup(),
					() -> createCounter("quartz.job.error.count", jobSignature)).increment();
		}
	}

	@Override
	public void afterScheduling(long startTime, JobSignature jobSignature, String[] reasons) {
		MapUtils.getOrCreate(rungingCounters, jobSignature.getJobGroup(),
				() -> createCounter("quartz.job.running.count", jobSignature)).increment(-1);
		MapUtils.getOrCreate(completedCounters, jobSignature.getJobGroup(),
				() -> createCounter("quartz.job.completed.count", jobSignature)).increment();
		if (ArrayUtils.isNotEmpty(reasons)) {
			MapUtils.getOrCreate(errorCounters, jobSignature.getJobGroup(),
					() -> createCounter("quartz.job.error.count", jobSignature)).increment();
		}
	}

	private Counter createCounter(String name, JobSignature jobSignature) {
		return meterRegistry.counter(name,
				Arrays.asList(Tag.of("job_group", jobSignature.getJobGroup()),
						Tag.of("env", env),
						Tag.of("instance", localHost + ":" + port)));
	}
}