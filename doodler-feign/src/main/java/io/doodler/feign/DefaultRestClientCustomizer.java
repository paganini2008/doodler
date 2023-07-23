package io.doodler.feign;

import static io.doodler.feign.RestClientConstants.DEFAULT_LOGGER_NAME;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.ObjectMapper;

import feign.Client;
import feign.Feign;
import feign.Logger;
import feign.Request.Options;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import feign.codec.StringDecoder;
import feign.jackson.JacksonDecoder;
import io.doodler.common.utils.Markers;
import io.doodler.feign.logger.ElkLogger;
import lombok.RequiredArgsConstructor;

/**
 * @Description: DefaultRestClientCustomizer
 * @Author: Fred Feng
 * @Date: 28/11/2022
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class DefaultRestClientCustomizer implements RestClientCustomizer {

    private final Client httpClient;
    private final RestClientProperties restClientProperties;
    private final ObjectMapper objectMapper;
    private final RequestInterceptorContainer requestInterceptorContainer;
    private final RestClientInterceptorContainer restClientInterceptorContainer;

    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    public void customize(Feign.Builder builder, String serviceId, String beanName, Class<?> interfaceClass,
                          Map<String, Object> attributes) {
        builder.client(getClient(serviceId, beanName, interfaceClass, attributes))
                .logger(getLogger(serviceId, beanName, interfaceClass, attributes))
                .logLevel(getLevel(serviceId, beanName, interfaceClass, attributes))
                .decoder(getDecoder(serviceId, beanName, interfaceClass, attributes))
                .errorDecoder(getErrorDecoder(serviceId, beanName, interfaceClass, attributes))
                .options(getOptions(serviceId, beanName, interfaceClass, attributes))
                .retryer(getRetryer(serviceId, beanName, interfaceClass, attributes))
                .requestInterceptors(getInterceptors(serviceId, beanName, interfaceClass, attributes));
    }

    protected Client getClient(String serviceId, String beanName, Class<?> interfaceClass,
                                     Map<String, Object> attributes) {
        return httpClient;
    }

    protected Logger getLogger(String serviceId, String beanName, Class<?> interfaceClass,
                               Map<String, Object> attributes) {
        return new ElkLogger(DEFAULT_LOGGER_NAME, Markers.forName(applicationName));
    }
    
    protected Logger.Level getLevel(String serviceId, String beanName, Class<?> interfaceClass,
                               Map<String, Object> attributes) {
		return Logger.Level.FULL;
	}

    protected Decoder getDecoder(String serviceId, String beanName, Class<?> interfaceClass,
                                 Map<String, Object> attributes) {
        MultipleTypeDecoder decoder = new MultipleTypeDecoder();
        decoder.setDefaultDecoder(new JacksonDecoder(objectMapper));
        decoder.addDecoder(String.class, new StringDecoder());
        return new CheckedResponseDecoder(decoder, restClientInterceptorContainer);
    }

    protected ErrorDecoder getErrorDecoder(String serviceId, String beanName, Class<?> interfaceClass,
                                           Map<String, Object> attributes) {
        return new GlobalErrorDecoder(restClientInterceptorContainer);
    }

    protected Options getOptions(String serviceId, String beanName, Class<?> interfaceClass,
                                 Map<String, Object> attributes) {
        long connectionTimeout = (Long) attributes.get("connectionTimeout");
        long readTimeout = (Long) attributes.get("readTimeout");
        TimeUnit timeUnit = (TimeUnit) attributes.get("timeUnit");
        boolean followRedirects = (Boolean) attributes.get("followRedirects");
        return new Options(Long.min(connectionTimeout, restClientProperties.getConnectionTimeout()), timeUnit,
                Long.min(readTimeout, restClientProperties.getReadTimeout()), timeUnit,
                followRedirects || restClientProperties.isFollowRedirects());
    }

    protected Retryer getRetryer(String serviceId, String beanName, Class<?> interfaceClass,
                                 Map<String, Object> attributes) {
        int retries = (Integer) attributes.get("retries");
        if (retries <= 0) {
            return Retryer.NEVER_RETRY;
        }
        return new Retryer.Default(3000L, 15L * 1000, retries);
    }

    protected List<RequestInterceptor> getInterceptors(String serviceId, String beanName, Class<?> interfaceClass,
                                                       Map<String, Object> attributes) {
        List<RequestInterceptor> list = new ArrayList<>();
        list.add(new RestfulRequestInterceptor());
        list.add(new SecurityRequestInterceptor(restClientProperties.getSecurity()));
        list.add(requestInterceptorContainer);
        list.add(restClientInterceptorContainer);
        return list;
    }
}