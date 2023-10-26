package io.doodler.feign.statistics;

import java.util.Map;

import feign.Client;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import io.doodler.feign.DefaultRestClientCustomizer;
import io.doodler.feign.EncoderDecoderFactory;
import io.doodler.feign.RequestInterceptorContainer;
import io.doodler.feign.RestClientInterceptorContainer;
import io.doodler.feign.RestClientProperties;
import io.doodler.feign.RetryFailureHandlerContainer;

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