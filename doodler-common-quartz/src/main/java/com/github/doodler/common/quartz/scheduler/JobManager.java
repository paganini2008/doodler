package com.github.doodler.common.quartz.scheduler;

import org.quartz.Scheduler;

/**
 * @Description: JobManager
 * @Author: Fred Feng
 * @Date: 13/06/2023
 * @Version 1.0.0
 */
public interface JobManager extends JobOperations, JobQuery {

    Scheduler getScheduler();

    void standby() throws Exception;

    void start() throws Exception;

    void stop() throws Exception;
}