package com.github.doodler.common.cloud.lb;

import java.io.IOException;
import java.net.URI;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.github.doodler.common.cloud.ServiceInstance;
import com.github.doodler.common.cloud.ServiceResourceAccessException;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: LbRestTemplate
 * @Author: Fred Feng
 * @Date: 15/06/2023
 * @Version 1.0.0
 */
@Slf4j
public class LbRestTemplate extends RestTemplate {

    public LbRestTemplate() {
        super();
    }

    private LoadBalancerClient loadBalancerClient;

    public void setLoadBalancerClient(LoadBalancerClient loadBalancerClient) {
        this.loadBalancerClient = loadBalancerClient;
    }

    @Override
    protected <T> T doExecute(URI originalUri, HttpMethod method, RequestCallback requestCallback,
            ResponseExtractor<T> responseExtractor) throws RestClientException {
        final String serviceId = originalUri.getHost();
        Assert.notNull(loadBalancerClient, "LoadBalancerClient must not be Null!");
        if (!loadBalancerClient.contains(serviceId)) {
            return super.doExecute(originalUri, method, requestCallback, responseExtractor);
        }
        Assert.state(serviceId != null,
                "Request URI does not contain a valid hostname: " + originalUri);
        ServiceInstance instance = loadBalancerClient.choose(serviceId, null);
        if (instance == null) {
            String message =
                    "LoadBalancer does not contain an instance for the service " + serviceId;
            if (log.isWarnEnabled()) {
                log.warn(message);
            }
            throw HttpServerErrorException.create(HttpStatus.SERVICE_UNAVAILABLE, message, null,
                    null, null);
        }
        log.info("Switch to service instance: {}", instance);
        URI reconstructedUri = loadBalancerClient.reconstructURI(instance, originalUri);
        try {
            return super.doExecute(reconstructedUri, method, requestCallback, responseExtractor);
        } catch (RestClientException e) {
            throw new ServiceResourceAccessException(serviceId, reconstructedUri, e.getMessage(),
                    (IOException) e.getCause());
        }
    }
}
