package com.github.doodler.common.quartz.scheduler;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Marker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestClientException;
import com.github.doodler.common.cloud.ApplicationInfo;
import com.github.doodler.common.cloud.ApplicationInfoHolder;
import com.github.doodler.common.cloud.DiscoveryClientService;
import com.github.doodler.common.cloud.ServiceResourceAccessException;
import com.github.doodler.common.quartz.executor.JobSignature;
import com.github.doodler.common.utils.ExceptionUtils;
import com.github.doodler.common.utils.IdUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: DispatcherJobBean
 * @Author: Fred Feng
 * @Date: 14/06/2023
 * @Version 1.0.0
 */
@Slf4j
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class DispatcherJobBean implements Job, JobSchedulingListenerAware {

    private static final String RETRY_GUID = "RETRY_GUID";
    private static final String RETRY_COUNTER = "RETRY_COUNTER";

    @Autowired
    private ApplicationInfoHolder applicationInfoHolder;

    @Autowired
    private JobLogService jobLogService;

    @Autowired
    private JobDispatcher jobDispatcher;

    @Autowired
    private Marker marker;

    @Autowired
    private DiscoveryClientService discoveryClientService;

    private List<JobSchedulingListener> jobSchedulingListeners = new CopyOnWriteArrayList<>();

    @Override
    public void setJobSchedulingListeners(List<JobSchedulingListener> jobSchedulingListeners) {
        this.jobSchedulingListeners = jobSchedulingListeners;
    }

    public void execute(JobExecutionContext context) throws JobExecutionException {
        boolean retry = false, callOk = false;
        Throwable reason = null;
        if (context.get(RETRY_GUID) != null) {
            retry = true;
        }
        final String guid = IdUtils.getShortUuid();

        JobSignature jobSignature = (JobSignature) context.getMergedJobDataMap().get("jobSignature");
        if (log.isInfoEnabled()) {
            log.info(marker, "Job server start to schedule job: {}", jobSignature);
        }
        long startTime = System.currentTimeMillis();
        for (JobSchedulingListener listener : jobSchedulingListeners) {
            listener.beforeScheduling(startTime, jobSignature);
        }

        String responseBody = null;
        try {
            jobLogService.startJob(guid, jobSignature, applicationInfoHolder.get());
            if (StringUtils.isNotBlank(jobSignature.getUrl())) {
                responseBody = jobDispatcher.directCall(guid, jobSignature, startTime);
            } else {
                responseBody = jobDispatcher.dispatch(guid, jobSignature, startTime);
            }
            callOk = true;
        } catch (RestClientException e) {
            reason = e;
            context.put(RETRY_GUID, guid);
            if (jobSignature.getMaxRetryCount() == null || jobSignature.getMaxRetryCount() < 0 ||
                    context.getRefireCount() < jobSignature.getMaxRetryCount()) {
                throw new SpecificJobExecutionException(e.getMessage(), e, true, context.getJobDetail().getKey());
            } else {
                if (log.isErrorEnabled()) {
                    log.error(e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            reason = e;
            context.put(RETRY_GUID, guid);
            if (jobSignature.getMaxRetryCount() == null || jobSignature.getMaxRetryCount() < 0 ||
                    context.getRefireCount() < jobSignature.getMaxRetryCount()) {
                throw new SpecificJobExecutionException(e.getMessage(), e, false, context.getJobDetail().getKey());
            } else {
                if (log.isErrorEnabled()) {
                    log.error(e.getMessage(), e);
                }
            }
        } finally {
            if (!callOk || StringUtils.isNotBlank(jobSignature.getUrl())) {
                ApplicationInfo jobExecutor = null;
                if (reason instanceof ServiceResourceAccessException) {
                    ServiceResourceAccessException srae = (ServiceResourceAccessException) reason;
                    URI uri = srae.getUri();
                    Optional<ApplicationInfo> opt = discoveryClientService.getApplicationInfo(srae.getServiceId(),
                            uri.getHost(), uri.getPort());
                    if (opt.isPresent()) {
                        jobExecutor = opt.get();
                    }
                }
                jobLogService.endJob(guid, jobSignature, jobExecutor, responseBody, ExceptionUtils.toArray(reason), retry);
                for (JobSchedulingListener listener : jobSchedulingListeners) {
                    listener.afterScheduling(startTime, jobSignature, reason);
                }
            }
            if (log.isInfoEnabled()) {
                log.info(marker, "Job server is complete to schedule job: {}", jobSignature);
            }
        }
    }

    @SuppressWarnings("unused")
    private int getRetryCount(JobExecutionContext context) {
        AtomicInteger counter = (AtomicInteger) context.get(RETRY_COUNTER);
        if (counter == null) {
            context.put(RETRY_COUNTER, new AtomicInteger());
            counter = (AtomicInteger) context.get(RETRY_COUNTER);
        }
        return counter.incrementAndGet();
    }
}