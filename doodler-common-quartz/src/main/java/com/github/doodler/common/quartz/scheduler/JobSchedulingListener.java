package com.github.doodler.common.quartz.scheduler;

import com.github.doodler.common.quartz.executor.JobSignature;

/**
 * @Description: JobSchedulingListener
 * @Author: Fred Feng
 * @Date: 20/11/2023
 * @Version 1.0.0
 */
public interface JobSchedulingListener {

    default void beforeScheduling(long startTime, JobSignature jobSignature) {
    }

    default void afterScheduling(long startTime, JobSignature jobSignature, Throwable reason) {
    }
    
    default void afterScheduling(long startTime, JobSignature jobSignature, String[] reasons) {
    }
}