package com.github.doodler.common.feign;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import com.github.doodler.common.feign.actuator.RestClientMetadataEndpoint;
import com.github.doodler.common.feign.logger.ElkLoggerAdapter;
import com.github.doodler.common.http.HttpComponentProperties;
import feign.Client;

/**
 * @Description: RestClientCandidatesAutoConfiguration
 * @Author: Fred Feng
 * @Date: 28/11/2022
 * @Version 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@Import({RestClientMetadataEndpoint.class, RestClientExceptionHandler.class})
@EnableConfigurationProperties({RestClientProperties.class})
public class RestClientCandidatesAutoConfiguration {

    @Bean
    public OkHttpClientFactory okHttpClient(HttpComponentProperties httpComponentProperties) {
        return new OkHttpClientFactory(httpComponentProperties);
    }

    @ConditionalOnMissingBean
    @Bean
    public EncoderDecoderFactory encoderDecoderFactory() {
        return new GenericEncoderDecoderFactory();
    }

    @Bean
    public RestClientInterceptorContainer restClientInterceptorContainer() {
        return new RestClientInterceptorContainer();
    }

    @Bean
    public RequestInterceptorContainer requestInterceptorContainer() {
        return new RequestInterceptorContainer();
    }

    @Bean
    public RetryFailureHandlerContainer retryFailureHandlerContainer() {
        return new RetryFailureHandlerContainer();
    }

    @Bean
    public RestClientMetadataCollector RestClientInfoCollector() {
        return new RestClientMetadataCollector();
    }

    @Bean
    public RestClientCustomizer defaultRestClientCustomizer(Client httpClient,
            EncoderDecoderFactory encoderDecoderFactory, RestClientProperties restClientProperties,
            RequestInterceptorContainer requestInterceptorContainer,
            RestClientInterceptorContainer restClientInterceptorContainer,
            RetryFailureHandlerContainer retryFailureHandlerContainer, BeanFactory beanFactory) {
        return new DefaultRestClientCustomizer(httpClient, encoderDecoderFactory,
                restClientProperties, requestInterceptorContainer, restClientInterceptorContainer,
                retryFailureHandlerContainer);
    }

    @Bean
    public RequestContextHolder requestContextHolder() {
        return new RequestContextHolder();
    }

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    public ElkLoggerAdapter elkLoggerAdapter() {
        return new ElkLoggerAdapter();
    }

    @ConditionalOnProperty(name = "api.trace.chain.enabled", havingValue = "true",
            matchIfMissing = true)
    @Bean
    public TraceableRestClientInterceptor traceableRestClientInterceptor() {
        return new TraceableRestClientInterceptor();
    }
}
