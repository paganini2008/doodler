package io.doodler.common.quartz.scheduler;

import io.doodler.common.quartz.executor.JobSignature;

/**
 * @Description: JobDispatcher
 * @Author: Fred Feng
 * @Date: 13/06/2023
 * @Version 1.0.0
 */
public interface JobDispatcher {

	String directCall(String guid, JobSignature jobTag);

	String dispatch(String guid, JobSignature jobTag);
}