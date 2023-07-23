package io.doodler.feign.statistic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import feign.Client;
import feign.RequestInterceptor;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import io.doodler.feign.DefaultRestClientCustomizer;
import io.doodler.feign.RequestInterceptorContainer;
import io.doodler.feign.RestClientInterceptorContainer;
import io.doodler.feign.RestClientProperties;

/**
 * @Description: ExactStatisticalRestClientCustomizer
 * @Author: Fred Feng
 * @Date: 29/01/2023
 * @Version 1.0.0
 */
public class ExactStatisticalRestClientCustomizer extends DefaultRestClientCustomizer {

    public ExactStatisticalRestClientCustomizer(Client httpClient,
                                                RestClientProperties restClientProperties,
                                                ObjectMapper objectMapper,
                                                RequestInterceptorContainer mappedRequestInterceptorContainer,
                                                RestClientInterceptorContainer restClientInterceptorContainer,
                                                AbstractRestClientStatisticsCollector restClientStatisticsCollector) {
        super(httpClient, restClientProperties, objectMapper, mappedRequestInterceptorContainer, restClientInterceptorContainer);
        this.restClientStatisticsCollector = restClientStatisticsCollector;
    }

    private final AbstractRestClientStatisticsCollector restClientStatisticsCollector;

    @Override
    protected Decoder getDecoder(String serviceId, String beanName, Class<?> interfaceClass,
                                 Map<String, Object> attributes) {
        return new StatisticalDecoder(super.getDecoder(serviceId, beanName, interfaceClass, attributes),
                restClientStatisticsCollector.getRestClientStatistics());
    }

    @Override
    protected ErrorDecoder getErrorDecoder(String serviceId, String beanName, Class<?> interfaceClass,
                                           Map<String, Object> attributes) {
        return new StatisticalErrorDecoder(super.getErrorDecoder(serviceId, beanName, interfaceClass, attributes),
                restClientStatisticsCollector.getRestClientStatistics());
    }

    @Override
    protected List<RequestInterceptor> getInterceptors(String serviceId, String beanName, Class<?> interfaceClass,
                                                       Map<String, Object> attributes) {
        List<RequestInterceptor> list = new ArrayList<>();
        list.addAll(super.getInterceptors(serviceId, beanName, interfaceClass, attributes));
        if (restClientStatisticsCollector instanceof RequestInterceptor) {
            list.add((RequestInterceptor) restClientStatisticsCollector);
        }
        return list;
    }
}