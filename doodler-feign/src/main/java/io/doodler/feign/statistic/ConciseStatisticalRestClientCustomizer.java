package io.doodler.feign.statistic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import feign.Client;
import feign.RequestInterceptor;
import io.doodler.feign.DefaultRestClientCustomizer;
import io.doodler.feign.RequestInterceptorContainer;
import io.doodler.feign.RestClientInterceptorContainer;
import io.doodler.feign.RestClientProperties;

/**
 * @Description: ConciseStatisticalRestClientCustomizer
 * @Author: Fred Feng
 * @Date: 29/01/2023
 * @Version 1.0.0
 */
public class ConciseStatisticalRestClientCustomizer extends DefaultRestClientCustomizer {

    public ConciseStatisticalRestClientCustomizer(Client httpClient,
                                                 RestClientProperties restClientProperties,
                                                 ObjectMapper objectMapper,
                                                 RequestInterceptorContainer mappedRequestInterceptorContainer,
                                                 RestClientInterceptorContainer restClientInterceptorContainer) {
        super(httpClient, restClientProperties, objectMapper, mappedRequestInterceptorContainer, restClientInterceptorContainer);
    }

    @Override
    protected List<RequestInterceptor> getInterceptors(String serviceId, String beanName, Class<?> interfaceClass,
            Map<String, Object> attributes) {
        List<RequestInterceptor> list = new ArrayList<>();
        list.addAll(super.getInterceptors(serviceId, beanName, interfaceClass, attributes));
        return list;
    }
}