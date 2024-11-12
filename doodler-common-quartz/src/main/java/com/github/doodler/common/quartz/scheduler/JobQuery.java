package com.github.doodler.common.quartz.scheduler;

import java.util.List;
import com.github.doodler.common.quartz.executor.JobDefination;
import com.github.doodler.common.quartz.executor.TriggerDefination;

/**
 * @Description: JobQuery
 * @Author: Fred Feng
 * @Date: 20/10/2023
 * @Version 1.0.0
 */
public interface JobQuery {

    List<JobGroupStatusVo> queryForJobGroupStatus() throws Exception;

    List<TriggerGroupStatusVo> queryForTriggerGroupStatus() throws Exception;

    JobGroupStatusVo getJobGroupStatus(String jobGroup) throws Exception;

    TriggerGroupStatusVo getTriggerGroupStatus(String triggerGroup) throws Exception;

    long getRunningJobCount(String jobGroup) throws Exception;

    long getRunningJobCount() throws Exception;

    List<String> getJobGroupNames() throws Exception;

    List<String> getTriggerGroupNames() throws Exception;

    default long countOfJobs(String jobGroup) throws Exception {
        return countOfJobs(jobGroup, null);
    }

    long countOfJobs(String jobGroup, String jobNamePattern) throws Exception;

    default long countOfTriggers(String triggerGroup) throws Exception {
        return countOfTriggers(triggerGroup, null);
    }

    long countOfTriggers(String triggerGroup, String triggerNamePattern) throws Exception;

    long countOfTriggersOfJob(String jobName, String jobGroup) throws Exception;

    List<TriggerDefination> queryForTriggerOfJob(String jobName, String jobGroup) throws Exception;

    List<TriggerStatusVo> queryForTriggerStatusOfJob(String jobName, String jobGroup) throws Exception;

    List<JobDefination> queryForJob(String triggerGroup) throws Exception;

    JobDefination queryForOneJob(String jobName, String jobGroup) throws Exception;

    List<TriggerDefination> queryForTrigger(String triggerGroup) throws Exception;

    TriggerDefination queryForOneTrigger(String triggerName, String triggerGroup) throws Exception;

    TriggerStatusVo queryForOneTriggerStatus(String triggerName, String triggerGroup) throws Exception;

    List<TriggerStatusVo> queryForTriggerStatus(String triggerGroup) throws Exception;

    PageVo<JobStatusVo> pageForJobStatus(String jobGroup, String jobNamePattern, int pageNumber,
                                         int pageSize) throws Exception;

    PageVo<JobDefination> pageForJob(String jobGroup, String jobNamePattern, int pageNumber, int pageSize) throws Exception;

    PageVo<TriggerDefination> pageForTrigger(String triggerGroup, String triggerNamePattern, int pageNumber, int pageSize)
            throws Exception;

    PageVo<TriggerStatusVo> pageForTriggerStatus(String triggerGroup, String triggerNamePattern, int pageNumber,
                                                 int pageSize) throws Exception;
}