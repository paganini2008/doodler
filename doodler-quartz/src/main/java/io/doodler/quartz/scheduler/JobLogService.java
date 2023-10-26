package io.doodler.quartz.scheduler;

import java.time.LocalDateTime;

import org.apache.commons.lang3.ArrayUtils;

import io.doodler.common.utils.BeanCopyUtils;
import io.doodler.discovery.ApplicationInfo;
import io.doodler.quartz.executor.JobSignature;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: JobLogService
 * @Author: Fred Feng
 * @Date: 15/06/2023
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public abstract class JobLogService {

    public void startJob(String guid, JobSignature jobSignature, ApplicationInfo jobScheduler) {
        JobLog jobLog = BeanCopyUtils.copyBean(jobSignature, JobLog.class);
        jobLog.setGuid(guid);
        jobLog.setSchedulerInstance(getInstance(jobScheduler));
        jobLog.setStartTime(LocalDateTime.now());
        try {
        	writeLog(jobLog);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public void endJob(String guid, JobSignature jobSignature, ApplicationInfo jobExecutor, String[] errors, boolean retry) {
        JobLog jobLog = BeanCopyUtils.copyBean(jobSignature, JobLog.class);
        jobLog.setGuid(guid);
        jobLog.setStatus(ArrayUtils.isNotEmpty(errors) ? 0 : 1);
        jobLog.setExecutorInstance(getInstance(jobExecutor));
        jobLog.setErrors(errors);
        jobLog.setEndTime(LocalDateTime.now());
        jobLog.setRetry(retry);
        try {
        	writeLog(jobLog);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
    }

    protected abstract void writeLog(JobLog jobLog) throws Exception;
    
    public abstract PageVo<JobLog> readLog(JobLogQuery logQuery) throws Exception;

    private String getInstance(ApplicationInfo applicationInfo) {
        if (applicationInfo != null) {
            return applicationInfo.getHost() + ":" + applicationInfo.getPort();
        }
        return null;
    }
}