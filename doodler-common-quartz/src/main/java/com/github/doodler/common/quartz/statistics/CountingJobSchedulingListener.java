package com.github.doodler.common.quartz.statistics;

import org.apache.commons.lang3.ArrayUtils;

import com.github.doodler.common.quartz.executor.JobSignature;
import com.github.doodler.common.quartz.scheduler.JobSchedulingListener;

import lombok.RequiredArgsConstructor;

/**
 * @Description: CountingJobSchedulingListener
 * @Author: Fred Feng
 * @Date: 20/11/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class CountingJobSchedulingListener implements JobSchedulingListener {

    private final CountingStatisticsService statisticsService;

    @Override
    public void beforeScheduling(long startTime, JobSignature jobSignature) {
        statisticsService.update("all", jobSignature.getJobGroup(), startTime, sampler -> {
            sampler.getSample().incrementRunningCount();
        });
    }

    @Override
    public void afterScheduling(long startTime, JobSignature jobSignature, Throwable reason) {
        statisticsService.update("all", jobSignature.getJobGroup(), startTime, sampler -> {
            sampler.getSample().decrementRunningCount();
            sampler.getSample().incrementCount();
            if (reason != null) {
                sampler.getSample().incrementErrorCount();
            }
        });
    }

    @Override
    public void afterScheduling(long startTime, JobSignature jobSignature, String[] reasons) {
        statisticsService.update("all", jobSignature.getJobGroup(), startTime, sampler -> {
            sampler.getSample().decrementRunningCount();
            sampler.getSample().incrementCount();
            if (ArrayUtils.isNotEmpty(reasons)) {
                sampler.getSample().incrementErrorCount();
            }
        });
    }
}