package com.github.doodler.common.quartz.scheduler;

import org.quartz.Scheduler;

/**
 * @Description: SchedulerCustomizer
 * @Author: Fred Feng
 * @Date: 21/09/2023
 * @Version 1.0.0
 */
@FunctionalInterface
public interface SchedulerCustomizer {

	void customize(Scheduler scheduler);
}