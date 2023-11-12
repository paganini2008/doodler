package io.doodler.common.quartz.executor;

import static io.doodler.common.quartz.JobConstants.DEFAULT_JOB_GROUP_NAME;
import static io.doodler.common.quartz.JobConstants.DEFAULT_JOB_TRIGGER_GROUP_NAME;

import java.util.Date;

import org.slf4j.Marker;
import org.springframework.beans.factory.annotation.Value;

import io.doodler.common.quartz.scheduler.JobOperations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: SimpleJobTemplate
 * @Author: Fred Feng
 * @Date: 22/08/2023
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class SimpleJobTemplate {

    private static final String DEFAULT_TRIGGER_NAME_PATTERN = "%s-TRIGGER";

    private final JobOperations jobOperations;
    private final Marker marker;

    @Value("${spring.application.name}")
    private String applicationName;

    private String defaultJobGroupName = DEFAULT_JOB_GROUP_NAME;

    private String defaultTriggerGroupName = DEFAULT_JOB_TRIGGER_GROUP_NAME;

    public void setDefaultJobGroupName(String defaultJobGroupName) {
        this.defaultJobGroupName = defaultJobGroupName;
    }

    public void setDefaultTriggerGroupName(String defaultTriggerGroupName) {
        this.defaultTriggerGroupName = defaultTriggerGroupName;
    }

    public Date addJob(String jobName, String description, String className, String method,
                                String initialParameter,
                                Date startTime, long period, Date endTime) throws Exception {
        JobDefination.JobDefinationBuilder builder = JobDefination.builder();
        JobDefination jobDefination = builder.applicationName(applicationName)
                .jobName(jobName)
                .description(description)
                .jobGroup(defaultJobGroupName)
                .className(className)
                .method(method)
                .initialParameter(initialParameter).build();
        TriggerDefination.TriggerDefinationBuilder triggerBuilder = TriggerDefination.builder();
        TriggerDefination triggerDefination = triggerBuilder.triggerName(
                        String.format(DEFAULT_TRIGGER_NAME_PATTERN, jobName.toUpperCase()))
                .triggerGroup(defaultTriggerGroupName)
                .startTime(startTime)
                .period(period)
                .endTime(endTime).build();
        jobDefination.setTriggerDefination(triggerDefination);
        Date firstFiredDate = jobOperations.addJob(jobDefination);
        if (log.isInfoEnabled()) {
            log.info(marker, "Job {}.{} is added and first fired date will at {}", jobDefination.getJobGroup(),
                    jobDefination.getJobName(), (firstFiredDate != null ? firstFiredDate.toString() : "<NONE>"));
        }
        return firstFiredDate;
    }

    public Date addJob(String jobName, String description, String className, String method,
                                String initialParameter,
                                Date startTime, long period, int repeatCount) throws Exception {
        JobDefination.JobDefinationBuilder builder = JobDefination.builder();
        JobDefination jobDefination = builder.applicationName(applicationName)
                .jobName(jobName)
                .description(description)
                .jobGroup(defaultJobGroupName)
                .className(className)
                .method(method)
                .initialParameter(initialParameter).build();
        TriggerDefination.TriggerDefinationBuilder triggerBuilder = TriggerDefination.builder();
        TriggerDefination triggerDefination = triggerBuilder.triggerName(
                        String.format(DEFAULT_TRIGGER_NAME_PATTERN, jobName.toUpperCase()))
                .triggerGroup(defaultTriggerGroupName)
                .startTime(startTime)
                .period(period)
                .repeatCount(repeatCount).build();
        jobDefination.setTriggerDefination(triggerDefination);
        Date firstFiredDate = jobOperations.addJob(jobDefination);
        if (log.isInfoEnabled()) {
            log.info(marker, "Job {}.{} is added and first fired date will at {}", jobDefination.getJobGroup(),
                    jobDefination.getJobName(), (firstFiredDate != null ? firstFiredDate.toString() : "<NONE>"));
        }
        return firstFiredDate;
    }

    public Date addCronJob(String jobName, String description, String className, String method,
                                    String initialParameter,
                                    String cron) throws Exception {
        JobDefination.JobDefinationBuilder builder = JobDefination.builder();
        JobDefination jobDefination = builder.applicationName(applicationName)
                .jobName(jobName)
                .description(description)
                .jobGroup(defaultJobGroupName)
                .className(className)
                .method(method)
                .initialParameter(initialParameter).build();
        TriggerDefination.TriggerDefinationBuilder triggerBuilder = TriggerDefination.builder();
        TriggerDefination triggerDefination = triggerBuilder.triggerName(
                        String.format(DEFAULT_TRIGGER_NAME_PATTERN, jobName.toUpperCase()))
                .triggerGroup(defaultTriggerGroupName)
                .cron(cron).build();
        jobDefination.setTriggerDefination(triggerDefination);
        Date firstFiredDate = jobOperations.addCronJob(jobDefination);
        if (log.isInfoEnabled()) {
            log.info(marker, "Job {}.{} is added and first fired date will at {}", jobDefination.getJobGroup(),
                    jobDefination.getJobName(), (firstFiredDate != null ? firstFiredDate.toString() : "<NONE>"));
        }
        return firstFiredDate;
    }

    public boolean isJobExists(String jobName) throws Exception {
        return jobOperations.isJobExists(jobName, defaultJobGroupName);
    }

    public void deleteJob(String jobName) throws Exception {
        jobOperations.deleteJob(jobName, defaultJobGroupName);
    }

    public void pauseJob(String jobName) throws Exception {
        jobOperations.pauseJob(jobName, defaultJobGroupName);
    }

    public void resumeJob(String jobName) throws Exception {
        jobOperations.resumeJob(jobName, defaultJobGroupName);
    }

    public Date modifyTrigger(String jobName, String cron, Date startTime, Date endTime)
            throws Exception {
    	Date firstFiredDate = jobOperations.modifyTrigger(
                String.format(DEFAULT_TRIGGER_NAME_PATTERN, jobName.toUpperCase()),
                defaultTriggerGroupName, cron, startTime, endTime);
        if (log.isInfoEnabled()) {
            log.info(marker, "Job {}.{} is updated and first fired date will at {}", defaultJobGroupName, jobName,
                    (firstFiredDate != null ? firstFiredDate.toString() : "<NONE>"));
        }
        return firstFiredDate;
    }

    public Date modifyTrigger(String jobName, Date startTime, long period, int repeatCount,
    		Date endTime) throws Exception {
    	Date firstFiredDate = jobOperations.modifyTrigger(
                String.format(DEFAULT_TRIGGER_NAME_PATTERN, jobName.toUpperCase()),
                defaultTriggerGroupName, startTime, period, repeatCount, endTime);
        if (log.isInfoEnabled()) {
            log.info(marker, "Job {}.{} is updated and first fired date will at {}", defaultJobGroupName, jobName,
                    (firstFiredDate != null ? firstFiredDate.toString() : "<NONE>"));
        }
        return firstFiredDate;
    }
}