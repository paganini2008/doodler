package com.github.doodler.common.quartz.executor;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import com.github.doodler.common.ApiResult;
import com.github.doodler.common.cloud.ApplicationInfoHolder;
import com.github.doodler.common.cloud.lb.LbRestTemplate;
import com.github.doodler.common.quartz.scheduler.JobSchedulingException;
import com.github.doodler.common.utils.ExceptionUtils;
import com.github.doodler.common.utils.MapUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: JobService
 * @Author: Fred Feng
 * @Date: 14/06/2023
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class JobService {

    private final JobBeanFactory jobBeanFactory;
    private final ApplicationInfoHolder applicationInfoHolder;
    private final LbRestTemplate restTemplate;

    @Value("${discovery.client.ping.usePublicIp:false}")
    private boolean usePublicIp;

    private final List<JobExecutionListener> listeners = new CopyOnWriteArrayList<>();

    @Async
    public void startJob(RpcJobBean rpcJobBean) {
        if (log.isTraceEnabled()) {
            log.trace("Start to run job: {}", rpcJobBean.getJobSignature());
        }
        listeners.forEach(l -> l.onStart(rpcJobBean));

        JobSignature jobSignature = rpcJobBean.getJobSignature();
        Object targetBean = jobBeanFactory.getBean(jobSignature.getClassName());
        Object result = null;
        boolean success = false;
        Throwable error = null;
        try {
            if (StringUtils.isNotBlank(jobSignature.getInitialParameter())) {
                if ("<NONE>".equalsIgnoreCase(jobSignature.getInitialParameter())) {
                    result = MethodUtils.invokeMethod(targetBean, true, jobSignature.getMethod(),
                            new Object[]{""}, new Class<?>[]{String.class});
                } else {
                    result = MethodUtils.invokeMethod(targetBean, true, jobSignature.getMethod(),
                            jobSignature.getInitialParameter());
                }
            } else {
                result = MethodUtils.invokeMethod(targetBean, true, jobSignature.getMethod());
            }
            success = true;
        } catch (Exception e) {
            error = e;
            throw new JobSchedulingException(e.getMessage(), e);
        } finally {
            if (success) {
                String callbackMethod = jobSignature.getMethod() + "Callback";
                try {
                    MethodUtils.invokeMethod(targetBean, true, callbackMethod, result);
                } catch (NoSuchMethodException ignored) {

                } catch (Exception e) {
                    if (log.isErrorEnabled()) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
            Throwable errorRef;
            if (error instanceof InvocationTargetException) {
                errorRef = ((InvocationTargetException) error).getTargetException();
            } else {
                errorRef = error;
            }
            String returnValue = result != null ? result.toString() : null;
            listeners.forEach(l -> l.onEnd(rpcJobBean, returnValue, errorRef));
            endJob(rpcJobBean, returnValue, errorRef);
        }
    }

    private void endJob(RpcJobBean rpcJobBean, String returnValue, Throwable error) {
        URI uri = URI.create(String.format("http://%s/job/end",
                rpcJobBean.getJobScheduler().getApplicationName()));
        RpcJobBean newRpcJobBean = new RpcJobBean(rpcJobBean.getGuid(), rpcJobBean.getJobSignature(),
                rpcJobBean.getStartTime());
        newRpcJobBean.setJobScheduler(rpcJobBean.getJobScheduler());
        newRpcJobBean.setJobExecutor(applicationInfoHolder.get());
        newRpcJobBean.setResponseBody(returnValue);
        newRpcJobBean.setErrors(error != null ? ExceptionUtils.toArray(error) : null);
        ResponseEntity<ApiResult<String>> responseEntity = null;
        try {
            responseEntity = restTemplate.exchange(uri, HttpMethod.POST,
                    new HttpEntity<Object>(newRpcJobBean), new ParameterizedTypeReference<ApiResult<String>>() {
                    });
        } catch (RuntimeException e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
        if (responseEntity != null && responseEntity.getStatusCode() == HttpStatus.OK) {
            if (log.isTraceEnabled()) {
                log.trace("End job: {}", rpcJobBean.getJobSignature());
            }
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Map<String, JobExecutionListener> map = event.getApplicationContext().getBeansOfType(JobExecutionListener.class);
        if (MapUtils.isNotEmpty(map)) {
            listeners.addAll(map.values());
        }
    }
}