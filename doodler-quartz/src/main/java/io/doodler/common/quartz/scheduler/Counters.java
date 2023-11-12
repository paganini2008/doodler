package io.doodler.common.quartz.scheduler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.doodler.common.utils.MapUtils;

/**
 * @Description: Counters
 * @Author: Fred Feng
 * @Date: 11/11/2023
 * @Version 1.0.0
 */
public class Counters {

	private final Map<String, Counter> data = new ConcurrentHashMap<>();

	public Counter getCounter(String jobGroup) {
		return MapUtils.getOrCreate(data, jobGroup, Counter::new);
	}
}