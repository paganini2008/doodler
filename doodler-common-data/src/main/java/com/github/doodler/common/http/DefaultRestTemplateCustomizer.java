package com.github.doodler.common.http;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.core.Ordered;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

/**
 * 
 * @Description: DefaultRestTemplateCustomizer
 * @Author: Fred Feng
 * @Date: 21/07/2024
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class DefaultRestTemplateCustomizer implements RestTemplateCustomizer, Ordered {

    private final ClientHttpRequestFactory clientHttpRequestFactory;
    private final List<ClientHttpRequestInterceptor> interceptors;

    @Override
    public void customize(RestTemplate restTemplate) {
        restTemplate.setRequestFactory(clientHttpRequestFactory);
        restTemplate.setInterceptors(interceptors);

        applySettings(restTemplate);
    }

    protected void applySettings(RestTemplate restTemplate) {
        List<HttpMessageConverter<?>> list = restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> converter : list) {
            if (converter instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter) converter).setDefaultCharset(StandardCharsets.UTF_8);
                break;
            }
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}
