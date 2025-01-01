package com.github.doodler.common.http;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.core.Ordered;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;
import com.github.doodler.common.retry.RetryableRestTemplate;
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
    private final RetryTemplate retryTemplate;

    private Charset charset = StandardCharsets.UTF_8;

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    @Override
    public void customize(RestTemplate restTemplate) {
        ClientHttpRequestFactory clientHttpRequestFactory = this.clientHttpRequestFactory;
        if (!(clientHttpRequestFactory instanceof BufferingClientHttpRequestFactory)) {
            clientHttpRequestFactory =
                    new BufferingClientHttpRequestFactory(clientHttpRequestFactory);
        }
        restTemplate.setRequestFactory(clientHttpRequestFactory);
        if (interceptors != null) {
            restTemplate.getInterceptors().addAll(interceptors);
        }
        List<HttpMessageConverter<?>> list = restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> converter : list) {
            if (converter instanceof StringHttpMessageConverter) {
                if (!((StringHttpMessageConverter) converter).getDefaultCharset().equals(charset)) {
                    ((StringHttpMessageConverter) converter).setDefaultCharset(charset);
                }
                break;
            }
        }
        if (restTemplate instanceof RetryableRestTemplate) {
            ((RetryableRestTemplate) restTemplate).setRetryTemplate(retryTemplate);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }



}
