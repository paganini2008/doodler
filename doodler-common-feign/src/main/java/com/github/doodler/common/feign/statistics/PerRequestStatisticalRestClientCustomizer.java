package com.github.doodler.common.feign.statistics;

import java.util.Map;
import com.github.doodler.common.feign.DefaultRestClientCustomizer;
import com.github.doodler.common.feign.EncoderDecoderFactory;
import com.github.doodler.common.feign.RequestInterceptorContainer;
import com.github.doodler.common.feign.RestClientInterceptorContainer;
import com.github.doodler.common.feign.RestClientProperties;
import com.github.doodler.common.feign.RetryFailureHandlerContainer;
import feign.Client;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;

/**
 * @Description: PerRequestStatisticalRestClientCustomizer
 * @Author: Fred Feng
 * @Date: 29/01/2023
 * @Version 1.0.0
 */
public class PerRequestStatisticalRestClientCustomizer extends DefaultRestClientCustomizer {

    public PerRequestStatisticalRestClientCustomizer(Client httpClient,
    		                                    EncoderDecoderFactory encoderDecoderFactory,
                                                RestClientProperties restClientProperties,
                                                RequestInterceptorContainer requestInterceptorContainer,
                                                RestClientInterceptorContainer restClientInterceptorContainer,
                                                RestClientStatisticsService statisticsService,
                                                RetryFailureHandlerContainer retryFailureHandlerContainer) {
        super(httpClient, encoderDecoderFactory, restClientProperties, requestInterceptorContainer, restClientInterceptorContainer, retryFailureHandlerContainer);
        this.statisticsService = statisticsService;
    }

    private final RestClientStatisticsService statisticsService;

    @Override
    protected Decoder getDecoder(String serviceId, String beanName, Class<?> interfaceClass,
                                 Map<String, Object> attributes) {
        return new StatisticalDecoder(super.getDecoder(serviceId, beanName, interfaceClass, attributes),
        		statisticsService);
    }

    @Override
    protected ErrorDecoder getErrorDecoder(String serviceId, String beanName, Class<?> interfaceClass,
                                           Map<String, Object> attributes) {
        return new StatisticalErrorDecoder(super.getErrorDecoder(serviceId, beanName, interfaceClass, attributes),
        		statisticsService);
    }
}