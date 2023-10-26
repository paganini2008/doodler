package io.doodler.feign.statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import feign.Client;
import feign.RequestInterceptor;
import io.doodler.feign.DefaultRestClientCustomizer;
import io.doodler.feign.EncoderDecoderFactory;
import io.doodler.feign.RequestInterceptorContainer;
import io.doodler.feign.RestClientInterceptorContainer;
import io.doodler.feign.RestClientProperties;
import io.doodler.feign.RetryFailureHandlerContainer;

/**
 * @Description: PerMethodStatisticalRestClientCustomizer
 * @Author: Fred Feng
 * @Date: 29/01/2023
 * @Version 1.0.0
 */
public class PerMethodStatisticalRestClientCustomizer extends DefaultRestClientCustomizer {

    public PerMethodStatisticalRestClientCustomizer(Client httpClient,
    		                                     EncoderDecoderFactory encoderDecoderFactory,
                                                 RestClientProperties restClientProperties,
                                                 RequestInterceptorContainer requestInterceptorContainer,
                                                 RestClientInterceptorContainer restClientInterceptorContainer,
                                                 RetryFailureHandlerContainer retryFailureHandlerContainer) {
        super(httpClient,
              encoderDecoderFactory, 
              restClientProperties, 
              requestInterceptorContainer, 
              restClientInterceptorContainer,
              retryFailureHandlerContainer);
    }

    @Override
    protected List<RequestInterceptor> getInterceptors(String serviceId, String beanName, Class<?> interfaceClass,
            Map<String, Object> attributes) {
        List<RequestInterceptor> list = new ArrayList<>();
        list.addAll(super.getInterceptors(serviceId, beanName, interfaceClass, attributes));
        return list;
    }
}