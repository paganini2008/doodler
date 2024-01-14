package com.github.doodler.common.http;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.github.doodler.common.retry.GenericRetryableRestTemplateFactory;
import com.github.doodler.common.retry.RetryableRestTemplateFactory;

import okhttp3.OkHttpClient;

/**
 * @Description: RestTemplateConfig
 * @Author: Fred Feng
 * @Date: 12/04/2023
 * @Version 1.0.0
 */
@AutoConfigureBefore(RestTemplateAutoConfiguration.class)
@ConditionalOnClass(RestTemplate.class)
@Configuration(proxyBeanMethods = false)
public class RestTemplateConfig {

    @ConditionalOnMissingBean
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder(defaultRestTemplateCustomizer()).build();
    }

    @ConditionalOnMissingBean
    @Bean
    public RestTemplateCustomizer defaultRestTemplateCustomizer() {
        return restTemplate -> {
            restTemplate.setRequestFactory(clientHttpRequestFactory());

            List<HttpMessageConverter<?>> list = restTemplate.getMessageConverters();
            for (HttpMessageConverter<?> converter : list) {
                if (converter instanceof StringHttpMessageConverter) {
                    ((StringHttpMessageConverter) converter).setDefaultCharset(StandardCharsets.UTF_8);
                    break;
                }
            }
        };
    }

    @ConditionalOnMissingBean
    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(false)
                .connectionPool(new okhttp3.ConnectionPool(200, 10,
                        TimeUnit.SECONDS))
                .connectTimeout(Duration.ofSeconds(10))
                .writeTimeout(Duration.ofSeconds(60)).readTimeout(
                        Duration.ofSeconds(60))
                .build();
        return new OkHttp3ClientHttpRequestFactory(okHttpClient);
    }

    @ConditionalOnMissingBean
    @Bean
    public RetryableRestTemplateFactory retryableRestTemplateFactory() {
        return new GenericRetryableRestTemplateFactory(defaultRestTemplateCustomizer());
    }
}