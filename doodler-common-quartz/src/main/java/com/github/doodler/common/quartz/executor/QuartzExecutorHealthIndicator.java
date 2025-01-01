package com.github.doodler.common.quartz.executor;

import static com.github.doodler.common.quartz.JobConstants.DEFAULT_JOB_SERVICE_NAME;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import com.github.doodler.common.ApiResult;
import com.github.doodler.common.cloud.ApplicationInfo;
import com.github.doodler.common.cloud.DiscoveryClientService;
import com.github.doodler.common.cloud.lb.LbRestTemplate;
import com.github.doodler.common.quartz.scheduler.JobGroupStatusVo;
import com.github.doodler.common.quartz.scheduler.JobOperationsException;
import com.github.doodler.common.quartz.scheduler.TriggerGroupStatusVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: QuartzExecutorHealthIndicator
 * @Author: Fred Feng
 * @Date: 15/10/2023
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class QuartzExecutorHealthIndicator extends AbstractHealthIndicator {

    private final DiscoveryClientService discoveryClientService;
    private final LbRestTemplate restTemplate;
    private final HttpHeaders defaultHttpHeaders;

    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    protected void doHealthCheck(Builder builder) throws Exception {
        Collection<ApplicationInfo> applicationInfos = discoveryClientService.getApplicationInfos(
                DEFAULT_JOB_SERVICE_NAME);
        if (CollectionUtils.isEmpty(applicationInfos)) {
            builder.down();
        } else {
            builder.up();
        }
        setJobGroupStatus(builder);
        setTriggerGroupStatus(builder);
        builder.build();
    }

    private void setJobGroupStatus(Builder builder) {
        try {
            ResponseEntity<ApiResult<JobGroupStatusVo>> responseEntity = restTemplate.exchange(
                    String.format("http://%s/job/man/group/status?jobGroup={jobGroup}", DEFAULT_JOB_SERVICE_NAME),
                    HttpMethod.GET, new HttpEntity<Object>(defaultHttpHeaders),
                    new ParameterizedTypeReference<ApiResult<JobGroupStatusVo>>() {
                    }, Collections.singletonMap("jobGroup", applicationName.toUpperCase()));
            if (responseEntity.getStatusCode().isError()) {
                throw new JobOperationsException("Job Server Error: " + responseEntity.toString());
            }
            JobGroupStatusVo jobGroupStatusVo = responseEntity.getBody().getData();
            builder.withDetail("jobGroup", jobGroupStatusVo);
        } catch (RestClientException e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private void setTriggerGroupStatus(Builder builder) {
        try {
            ResponseEntity<ApiResult<TriggerGroupStatusVo>> responseEntity = restTemplate.exchange(
                    String.format("http://%s/job/man/trigger/group/status?triggerGroup={triggerGroup}",
                            DEFAULT_JOB_SERVICE_NAME),
                    HttpMethod.GET, new HttpEntity<Object>(defaultHttpHeaders),
                    new ParameterizedTypeReference<ApiResult<TriggerGroupStatusVo>>() {
                    }, Collections.singletonMap("triggerGroup",
                            String.format("%s-TRIGGER-GROUP", applicationName.toUpperCase())));
            if (responseEntity.getStatusCode().isError()) {
                throw new JobOperationsException("Job Server Error: " + responseEntity.toString());
            }
            TriggerGroupStatusVo triggerGroupStatusVo = responseEntity.getBody().getData();
            builder.withDetail("triggerGroup", triggerGroupStatusVo);
        } catch (RestClientException e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
    }
}