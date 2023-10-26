package io.doodler.quartz.scheduler;

import io.doodler.quartz.executor.JobSignature;

/**
 * @Description: JobDispatcher
 * @Author: Fred Feng
 * @Date: 13/06/2023
 * @Version 1.0.0
 */
public interface JobDispatcher {

	void directCall(String guid, JobSignature jobTag);

	void dispatch(String guid, JobSignature jobTag);
}