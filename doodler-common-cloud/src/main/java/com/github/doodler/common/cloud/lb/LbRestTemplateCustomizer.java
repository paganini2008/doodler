package com.github.doodler.common.cloud.lb;

import java.util.List;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;
import com.github.doodler.common.http.DefaultRestTemplateCustomizer;

/**
 * 
 * @Description: LbRestTemplateCustomizer
 * @Author: Fred Feng
 * @Date: 01/01/2025
 * @Version 1.0.0
 */
public class LbRestTemplateCustomizer extends DefaultRestTemplateCustomizer {

    public LbRestTemplateCustomizer(ClientHttpRequestFactory clientHttpRequestFactory,
            List<ClientHttpRequestInterceptor> interceptors, LoadBalancerClient loadBalancerClient,
            RetryTemplate retryTemplate) {
        super(clientHttpRequestFactory, interceptors, retryTemplate);
        this.loadBalancerClient = loadBalancerClient;
        this.retryTemplate = retryTemplate;
    }

    private final LoadBalancerClient loadBalancerClient;
    private final RetryTemplate retryTemplate;

    @Override
    public void customize(RestTemplate restTemplate) {
        super.customize(restTemplate);

        if (restTemplate instanceof LbRestTemplate) {
            ((LbRestTemplate) restTemplate).setLoadBalancerClient(loadBalancerClient);
        }
        if (restTemplate instanceof RetryableLbRestTemplate) {
            ((RetryableLbRestTemplate) restTemplate).setRetryTemplate(retryTemplate);
        }
    }


}
