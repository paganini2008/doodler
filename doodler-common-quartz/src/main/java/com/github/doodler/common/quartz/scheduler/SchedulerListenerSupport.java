package com.github.doodler.common.quartz.scheduler;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.slf4j.Marker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

/**
 * @Description: SchedulerListenerSupport
 * @Author: Fred Feng
 * @Date: 16/06/2023
 * @Version 1.0.0
 */
@Slf4j
public class SchedulerListenerSupport implements SchedulerListener, ApplicationEventPublisherAware {

    @Autowired
    private Marker marker;

    @Setter
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void jobScheduled(Trigger trigger) {
        if (log.isInfoEnabled()) {
            log.info(marker, "Called by the Scheduler when a org.quartz.JobDetailis scheduled. Trigger: {}", trigger);
        }
        applicationEventPublisher.publishEvent(
                new SchedulerStateChangeEvent(this, trigger, SchedulerStateEventType.JOB_SCHEDULED));
    }

    @Override
    public void jobUnscheduled(TriggerKey triggerKey) {
        if (log.isInfoEnabled()) {
            log.info(marker, "Called by the Scheduler when a org.quartz.JobDetailis unscheduled. TriggerKey: {}",
                    triggerKey);
        }
        applicationEventPublisher.publishEvent(
                new SchedulerStateChangeEvent(this, triggerKey, SchedulerStateEventType.JOB_UNSCHEDULED));
    }

    @Override
    public void triggerFinalized(Trigger trigger) {
        if (log.isInfoEnabled()) {
            log.info(marker,
                    "Called by the Scheduler when a Trigger has reached the condition in which it will never fire again. Trigger: {}",
                    trigger);
        }
        applicationEventPublisher.publishEvent(
                new SchedulerStateChangeEvent(this, trigger, SchedulerStateEventType.TRIGGER_FINALIZED));
    }

    @Override
    public void triggerPaused(TriggerKey triggerKey) {
        if (log.isInfoEnabled()) {
            log.info(marker, "Called by the Scheduler when a Trigger has been paused. TriggerKey: {}", triggerKey);
        }
        applicationEventPublisher.publishEvent(
                new SchedulerStateChangeEvent(this, triggerKey, SchedulerStateEventType.TRIGGER_PAUSED));
    }

    @Override
    public void triggersPaused(String triggerGroup) {
        if (log.isInfoEnabled()) {
            log.info(marker, "Called by the Scheduler when agroup of Triggers has been paused. TriggerGroup: {}",
                    triggerGroup);
        }
        applicationEventPublisher.publishEvent(
                new SchedulerStateChangeEvent(this, triggerGroup, SchedulerStateEventType.TRIGGERS_PAUSED));
    }

    @Override
    public void triggerResumed(TriggerKey triggerKey) {
        if (log.isInfoEnabled()) {
            log.info(marker, "Called by the Scheduler when a Trigger has been un-paused. TriggerKey: {}", triggerKey);
        }
        applicationEventPublisher.publishEvent(
                new SchedulerStateChangeEvent(this, triggerKey, SchedulerStateEventType.TRIGGER_RESUMED));
    }

    @Override
    public void triggersResumed(String triggerGroup) {
        if (log.isInfoEnabled()) {
            log.info(marker, "Called by the Scheduler when agroup of Triggers has been un-paused. TriggerGroup: {}",
                    triggerGroup);
        }
        applicationEventPublisher.publishEvent(
                new SchedulerStateChangeEvent(this, triggerGroup, SchedulerStateEventType.TRIGGERS_RESUMED));
    }

    @Override
    public void jobAdded(JobDetail jobDetail) {
        if (log.isInfoEnabled()) {
            log.info(marker, "Called by the Scheduler when a org.quartz.JobDetail has been added. JobDetail: {}",
                    jobDetail);
        }
        applicationEventPublisher.publishEvent(
                new SchedulerStateChangeEvent(this, jobDetail, SchedulerStateEventType.JOB_ADDED));
    }

    @Override
    public void jobDeleted(JobKey jobKey) {
        if (log.isInfoEnabled()) {
            log.info(marker, "Called by the Scheduler when a org.quartz.JobDetail has been deleted. jobKey: {}", jobKey);
        }
        applicationEventPublisher.publishEvent(
                new SchedulerStateChangeEvent(this, jobKey, SchedulerStateEventType.JOB_DELETED));
    }

    @Override
    public void jobPaused(JobKey jobKey) {
        if (log.isInfoEnabled()) {
            log.info(marker, "Called by the Scheduler when a org.quartz.JobDetail has been paused. jobKey: {}", jobKey);
        }
        applicationEventPublisher.publishEvent(
                new SchedulerStateChangeEvent(this, jobKey, SchedulerStateEventType.JOB_PAUSED));
    }

    @Override
    public void jobsPaused(String jobGroup) {
        if (log.isInfoEnabled()) {
            log.info(marker, "Called by the Scheduler when agroup of org.quartz.JobDetails has been paused. JobGroup: {}",
                    jobGroup);
        }
        applicationEventPublisher.publishEvent(
                new SchedulerStateChangeEvent(this, jobGroup, SchedulerStateEventType.JOBS_PAUSED));
    }

    @Override
    public void jobResumed(JobKey jobKey) {
        if (log.isInfoEnabled()) {
            log.info(marker, "Called by the Scheduler when a org.quartz.JobDetailhas been un-paused. jobKey: {}", jobKey);
        }
        applicationEventPublisher.publishEvent(
                new SchedulerStateChangeEvent(this, jobKey, SchedulerStateEventType.JOB_RESUMED));
    }

    @Override
    public void jobsResumed(String jobGroup) {
        if (log.isInfoEnabled()) {
            log.info(marker,
                    "Called by the Scheduler when agroup of org.quartz.JobDetails has been un-paused. JobGroup: {}",
                    jobGroup);
        }
        applicationEventPublisher.publishEvent(
                new SchedulerStateChangeEvent(this, jobGroup, SchedulerStateEventType.JOBS_RESUMED));
    }

    @Override
    public void schedulerError(String msg, SchedulerException e) {
        if (log.isErrorEnabled()) {
            log.error(msg, e);
        }
        applicationEventPublisher.publishEvent(
                new SchedulerStateChangeEvent(this, e, SchedulerStateEventType.SCHEDULER_ERROR));
    }

    @Override
    public void schedulerInStandbyMode() {
        if (log.isInfoEnabled()) {
            log.info(marker, "Called by the Scheduler to inform the listener that it has move to standby mode. ");
        }
    }

    @Override
    public void schedulerStarted() {
        if (log.isInfoEnabled()) {
            log.info(marker, "Called by the Scheduler to inform the listener that it has started.");
        }
    }

    @Override
    public void schedulerStarting() {
        if (log.isInfoEnabled()) {
            log.info(marker, "Called by the Scheduler to inform the listener that it is starting.");
        }
    }

    @Override
    public void schedulerShutdown() {
        if (log.isInfoEnabled()) {
            log.info(marker, "Called by the Scheduler to inform the listener that it has shutdown.");
        }
    }

    @Override
    public void schedulerShuttingdown() {
        if (log.isInfoEnabled()) {
            log.info(marker, "Called by the Scheduler to inform the listener that it has begun the shutdown sequence. ");
        }
    }

    @Override
    public void schedulingDataCleared() {
        if (log.isInfoEnabled()) {
            log.info(marker,
                    "Called by the Scheduler to inform the listener that all jobs, triggers and calendars were deleted.");
        }
    }
}