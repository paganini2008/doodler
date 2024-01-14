package com.github.doodler.common.quartz.scheduler;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;

import lombok.RequiredArgsConstructor;

/**
 * @Description: QuartzSchedulerHealthIndicator
 * @Author: Fred Feng
 * @Date: 15/10/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class QuartzSchedulerHealthIndicator extends AbstractHealthIndicator {

    private final JobManager jobManager;

    @Override
    protected void doHealthCheck(Builder builder) throws Exception {
        
        long jobGroupCount = 0, jobCount = 0, runningJobCount = 0, completedJobCount = 0, errorCount = 0;
        long triggerGroupCount = 0, triggerCount = 0;
        List<JobGroupStatusVo> jobGroupStatusVos = jobManager.queryForJobGroupStatus();
        if (CollectionUtils.isNotEmpty(jobGroupStatusVos)) {
            jobGroupCount += jobGroupStatusVos.size();
            for (JobGroupStatusVo vo : jobGroupStatusVos) {
                jobCount += vo.getJobCount();
                runningJobCount += vo.getRunningJobCount();
                completedJobCount += vo.getCompletedJobCount();
                errorCount += vo.getErrorCount();
            }
        }

        List<TriggerGroupStatusVo> triggerGroupStatusVos = jobManager.queryForTriggerGroupStatus();
        if (CollectionUtils.isNotEmpty(triggerGroupStatusVos)) {
            triggerGroupCount += triggerGroupStatusVos.size();
            for (TriggerGroupStatusVo vo : triggerGroupStatusVos) {
                triggerCount += vo.getTriggerCount();
            }
        }
        double errorRate = (double) errorCount / completedJobCount;
        if (errorRate >= 0.8d) {
            builder.down();
        } else {
            builder.up();
        }
        builder.withDetail("jobGroupCount", jobGroupCount)
                .withDetail("jobCount", jobCount)
                .withDetail("triggerGroupCount", triggerGroupCount)
                .withDetail("triggerCount", triggerCount)
                .withDetail("runningJobCount", runningJobCount)
                .withDetail("completedJobCount", completedJobCount)
                .withDetail("errorCount", errorCount);
        builder.build();
    }
}