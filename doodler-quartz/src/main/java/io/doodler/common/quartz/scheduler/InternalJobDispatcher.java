package io.doodler.common.quartz.scheduler;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import io.doodler.common.ApiResult;
import io.doodler.common.discovery.ApplicationInfo;
import io.doodler.common.discovery.ApplicationInfoHolder;
import io.doodler.common.discovery.DiscoveryClientService;
import io.doodler.common.discovery.LoadBalancedRestTemplate;
import io.doodler.common.utils.JacksonUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import io.doodler.common.quartz.executor.JobSignature;
import io.doodler.common.quartz.executor.RpcJobBean;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: InternalJobDispatcher
 * @Author: Fred Feng
 * @Date: 14/06/2023
 * @Version 1.0.0
 */
@Slf4j
@Component
public class InternalJobDispatcher implements JobDispatcher {

    @Autowired
    private ApplicationInfoHolder applicationInfoHolder;

    @Autowired
    private LoadBalancedRestTemplate restTemplate;

    @Autowired
    private DiscoveryClientService discoveryClientService;

    @Override
    public String directCall(String guid, JobSignature jobSignature, long startTime) {
        HttpMethod httpMethod = HttpMethod.valueOf(jobSignature.getMethod());
        HttpHeaders httpHeaders = getHeaders(jobSignature);
        Map<String, Object> payload = Collections.emptyMap();
        if (StringUtils.isNotBlank(jobSignature.getInitialParameter())) {
            try {
                payload = JacksonUtils.parseJson(jobSignature.getInitialParameter(),
                        new TypeReference<Map<String, Object>>() {
                        });
            } catch (RuntimeException ignored) {
            }
        }

        ResponseEntity<String> responseEntity = null;
        try {
            switch (httpMethod) {
                case GET:
                case DELETE:
                    responseEntity = restTemplate.exchange(jobSignature.getUrl(),
                            httpMethod,
                            new HttpEntity<>(httpHeaders),
                            String.class, payload);
                    break;
                case PUT:
                case POST:
                    responseEntity = restTemplate.exchange(jobSignature.getUrl(),
                            httpMethod,
                            new HttpEntity<Object>(payload, httpHeaders),
                            String.class);
                    break;
                default:
                    throw new UnsupportedOperationException(httpMethod.name());
            }
        } catch (RestClientException e) {
            throw e;
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
        if(responseEntity == null) {
        	return null;
        }
        if (responseEntity.getStatusCode().isError()) {
            if (log.isErrorEnabled()) {
                log.error("Job Executor Response: {}", responseEntity.getBody().toString());
            }
        }
        return responseEntity.getBody().toString();
    }

    @Override
    public String dispatch(String guid, JobSignature jobSignature, long startTime) {
        Collection<ApplicationInfo> applicationInfos = discoveryClientService.getApplicationInfos(jobSignature.getJobExecutor());
        if (CollectionUtils.isEmpty(applicationInfos)) {
            throw new IllegalStateException(
                    "Unable to find out application info for job executor: " + jobSignature.getJobExecutor());
        }
        ApplicationInfo applicationInfo = IteratorUtils.first(applicationInfos.iterator());
        URI uri = URI.create(
                String.format("http://%s%s/job/start", jobSignature.getJobExecutor(),
                        StringUtils.isNotBlank(applicationInfo.getContextPath()) ? applicationInfo.getContextPath() : ""));

        RpcJobBean rpcJobBean = new RpcJobBean(guid, jobSignature, startTime);
        rpcJobBean.setJobScheduler(applicationInfoHolder.get());
        HttpHeaders httpHeaders = getHeaders(jobSignature);
        ResponseEntity<ApiResult<String>> responseEntity = null;
        try {
            responseEntity = restTemplate.exchange(uri, HttpMethod.POST,
                    new HttpEntity<Object>(rpcJobBean, httpHeaders),
                    new ParameterizedTypeReference<ApiResult<String>>() {
                    });
        } catch (RestClientException e) {
            throw e;
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
        if(responseEntity == null) {
        	return null;
        }
        if (responseEntity.getStatusCode().isError()) {
            if (log.isErrorEnabled()) {
                log.error("Job Executor Response: {}", responseEntity.getBody().toString());
            }
        }
        return responseEntity.getBody().toString();
    }

    private HttpHeaders getHeaders(JobSignature jobSignature) {
        HttpHeaders headers = new HttpHeaders();
        if (ArrayUtils.isNotEmpty(jobSignature.getDefaultHeaders())) {
            int index;
            for (String line : jobSignature.getDefaultHeaders()) {
                index = line.indexOf("=");
                if (index > 0) {
                    headers.add(line.substring(0, index), line.substring(index + 1));
                }
            }
        }
        return headers;
    }
}