package com.github.doodler.common.quartz.scheduler;

import java.util.Date;
import java.util.Map;

import org.springframework.lang.Nullable;
import com.github.doodler.common.quartz.executor.JobDefination;

/**
 * @Description: JobOperations
 * @Author: Fred Feng
 * @Date: 19/06/2023
 * @Version 1.0.0
 */
public interface JobOperations {

    Date addJob(JobDefination jobDefination) throws Exception;

    Date addCronJob(JobDefination jobDefination) throws Exception;

    Date referenceJob(JobDefination jobDefination) throws Exception;

    Date referenceCronJob(JobDefination jobDefination) throws Exception;

    Date modifyJob(JobDefination jobDefination) throws Exception;

    Date modifyCronJob(JobDefination jobDefination) throws Exception;

    Date modifyTrigger(String triggerName,
                       String triggerGroup,
                       Date startTime,
                       long period,
                       int repeatCount,
                       @Nullable Date endTime,
                       @Nullable Map<String, Object> dataMap) throws Exception;

    Date modifyTrigger(String triggerName,
                       String triggerGroup,
                       String cron,
                       Date startTime,
                       @Nullable Date endTime,
                       @Nullable Map<String, Object> dataMap) throws Exception;

    void pauseTrigger(String triggerName, String triggerGroup) throws Exception;

    void pauseTriggers(String triggerGroup) throws Exception;

    void pauseAll() throws Exception;

    void pauseJob(String jobName, String jobGroup) throws Exception;

    void pauseJobs(String jobGroup) throws Exception;

    boolean isJobExists(String jobName, String jobGroup) throws Exception;

    boolean isTriggerExists(String triggerName, String triggerGroup) throws Exception;

    boolean deleteTrigger(String triggerName, String triggerGroup) throws Exception;

    boolean deleteTriggers(String triggerGroup) throws Exception;

    boolean deleteJob(String jobName, String jobGroup) throws Exception;

    boolean deleteJobs(String jobGroup) throws Exception;

    void resumeTrigger(String triggerName, String triggerGroup) throws Exception;

    void resumeTriggers(String triggerGroup) throws Exception;

    void resumeAll() throws Exception;

    void resumeJob(String jobName, String jobGroup) throws Exception;

    void resumeJobs(String jobGroup) throws Exception;

    void runNow(String jobName, String jobGroup, String parameter) throws Exception;
}