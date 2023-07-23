package io.doodler.cluster;

import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.doodler.common.http.HttpRequest;
import io.doodler.common.http.HttpRequestBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: HttpPingStrategy
 * @Author: Fred Feng
 * @Date: 22/07/2023
 * @Version 1.0.0
 */
@Slf4j
@ConditionalOnMissingBean(PingStrategy.class)
@Component
public class HttpPingStrategy implements PingStrategy {

    private HttpRequest httpRequest;
    private HttpHeaders defaultHeaders = new HttpHeaders();
    private boolean usePublicIp;
    private int maxAttempts = 3;

    public void setUsePublicIp(boolean usePublicIp) {
        this.usePublicIp = usePublicIp;
    }

    public void setDefaultHeaders(HttpHeaders defaultHeaders) {
        this.defaultHeaders = defaultHeaders;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    @Override
    public boolean isAlive(ServiceInstance serviceInstance) {
        if (httpRequest == null) {
            httpRequest = HttpRequestBuilder.get()
                    .setHostUrl(serviceInstance.toHostUrl(usePublicIp))
                    .setGroup("ping")
                    .setMaxAttempts(maxAttempts)
                    .build();
        }
        ResponseEntity<Map<String, Object>> responseEntity;
        try {
            responseEntity = httpRequest.execute(serviceInstance.getHealthCheckUrl(), defaultHeaders,
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    });
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
            return false;
        }
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> data = responseEntity.getBody();
            return "UP".equalsIgnoreCase((String) data.get("status"));
        }
        return false;
    }
}