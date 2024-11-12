package com.github.doodler.common.http;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import com.github.doodler.common.retry.RetryableRestTemplate;
import okhttp3.OkHttpClient;

/**
 * @Description: RestTemplateConfig
 * @Author: Fred Feng
 * @Date: 12/04/2023
 * @Version 1.0.0
 */
@AutoConfigureBefore(RestTemplateAutoConfiguration.class)
@ConditionalOnClass(RestTemplate.class)
@EnableConfigurationProperties({HttpComponentProperties.class})
@Configuration(proxyBeanMethods = false)
public class RestTemplateConfig {

    @ConditionalOnMissingBean
    @Bean
    public RestTemplate defaultRestTemplate(RestTemplateCustomizer... customizers) {
        return new RestTemplateBuilder(customizers).configure(new RestTemplate());
    }

    @ConditionalOnMissingBean(name = "defaultRestTemplateCustomizer")
    @Bean
    public RestTemplateCustomizer defaultRestTemplateCustomizer(
            ClientHttpRequestFactory clientHttpRequestFactory,
            List<ClientHttpRequestInterceptor> interceptors) {
        return new DefaultRestTemplateCustomizer(clientHttpRequestFactory, interceptors);
    }

    @ConditionalOnMissingBean
    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory(HttpComponentProperties httpConfig) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().retryOnConnectionFailure(false)
                .connectTimeout(Duration.ofSeconds(httpConfig.getOkhttp().getConnectionTimeout()))
                .readTimeout(Duration.ofSeconds(httpConfig.getOkhttp().getReadTimeout()))
                .followRedirects(true)
                .connectionPool(
                        new okhttp3.ConnectionPool(httpConfig.getOkhttp().getMaxIdleConnections(),
                                httpConfig.getOkhttp().getKeepAliveDuration(), TimeUnit.SECONDS))
                .connectTimeout(Duration.ofSeconds(httpConfig.getOkhttp().getConnectionTimeout()))
                .readTimeout(Duration.ofSeconds(httpConfig.getOkhttp().getReadTimeout())).build();
        return new OkHttp3ClientHttpRequestFactory(okHttpClient);
    }

    @ConditionalOnMissingBean
    @Bean
    public RetryableRestTemplate retryableRestTemplate(RestTemplateCustomizer... customizers) {
        return new RestTemplateBuilder(customizers).configure(new RetryableRestTemplate());
    }

}
