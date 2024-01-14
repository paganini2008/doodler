package com.github.doodler.common.quartz.scheduler;

import java.util.List;

/**
 * @Description: JobSchedulingListenerAware
 * @Author: Fred Feng
 * @Date: 20/11/2023
 * @Version 1.0.0
 */
public interface JobSchedulingListenerAware {

	void setJobSchedulingListeners(List<JobSchedulingListener> jobSchedulingListeners);
}