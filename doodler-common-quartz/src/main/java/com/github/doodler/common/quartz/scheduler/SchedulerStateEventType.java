package com.github.doodler.common.quartz.scheduler;

/**
 * @Description: StateChangeEventType
 * @Author: Fred Feng
 * @Date: 28/09/2023
 * @Version 1.0.0
 */
public enum SchedulerStateEventType {

    JOB_SCHEDULED,

    JOB_UNSCHEDULED,

    TRIGGER_FINALIZED,

    TRIGGER_PAUSED,
    
    TRIGGERS_PAUSED,

    TRIGGER_RESUMED,
    
    TRIGGERS_RESUMED,

    JOB_ADDED,

    JOB_DELETED,

    JOB_PAUSED,

    JOBS_PAUSED,

    JOB_RESUMED,

    JOBS_RESUMED,

    SCHEDULER_ERROR;
}