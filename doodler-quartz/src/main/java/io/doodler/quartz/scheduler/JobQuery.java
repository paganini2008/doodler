package io.doodler.quartz.scheduler;

import java.util.List;

import io.doodler.quartz.executor.JobDefination;
import io.doodler.quartz.executor.TriggerDefination;

/**
 * @Description: JobQuery
 * @Author: Fred Feng
 * @Date: 20/10/2023
 * @Version 1.0.0
 */
public interface JobQuery {

    default int countOfJobs(String jobGroup) throws Exception {
        return countOfJobs(jobGroup, null);
    }

    int countOfJobs(String jobGroup, String jobNamePattern) throws Exception;

    default int countOfTriggers(String triggerGroup) throws Exception {
        return countOfTriggers(triggerGroup, null);
    }

    int countOfTriggers(String triggerGroup, String triggerNamePattern) throws Exception;

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