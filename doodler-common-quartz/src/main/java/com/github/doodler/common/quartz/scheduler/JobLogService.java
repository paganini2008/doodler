package com.github.doodler.common.quartz.scheduler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import com.github.doodler.common.cloud.ApplicationInfo;
import com.github.doodler.common.quartz.executor.JobSignature;
import com.github.doodler.common.utils.BeanCopyUtils;
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

    public void endJob(String guid, JobSignature jobSignature, ApplicationInfo jobExecutor, String responseBody,
                       String[] errors, boolean retry) {
        JobLog jobLog = BeanCopyUtils.copyBean(jobSignature, JobLog.class);
        jobLog.setGuid(guid);
        jobLog.setStatus(ArrayUtils.isNotEmpty(errors) ? 0 : 1);
        jobLog.setExecutorInstance(getInstance(jobExecutor));
        jobLog.setResponseBody(responseBody);
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

    public abstract List<Map<String, Object>> summarizeLog() throws Exception;

    private String getInstance(ApplicationInfo applicationInfo) {
        if (applicationInfo != null) {
            return applicationInfo.getHost() + ":" + applicationInfo.getPort();
        }
        return null;
    }
}