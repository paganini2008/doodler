package com.github.doodler.common.quartz.scheduler;

import com.github.doodler.common.quartz.executor.JobSignature;

/**
 * @Description: JobDispatcher
 * @Author: Fred Feng
 * @Date: 13/06/2023
 * @Version 1.0.0
 */
public interface JobDispatcher {

	String directCall(String guid, JobSignature jobSignature, long startTime);

	String dispatch(String guid, JobSignature jobSignature, long startTime);
}