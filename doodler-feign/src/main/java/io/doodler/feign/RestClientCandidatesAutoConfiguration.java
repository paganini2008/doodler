package io.doodler.feign;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.fasterxml.jackson.databind.ObjectMapper;

import feign.Client;
import io.doodler.feign.actuator.RestClientInfoEndpoint;
import io.doodler.feign.actuator.ServiceInstanceEndpoint;
import io.doodler.feign.logger.ElkLoggerAdapter;

/**
 * @Description: RestClientCandidatesAutoConfiguration
 * @Author: Fred Feng
 * @Date: 28/11/2022
 * @Version 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@Import({RestClientInfoEndpoint.class, ServiceInstanceEndpoint.class})
@EnableConfigurationProperties({RestClientProperties.class})
public class RestClientCandidatesAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public OkHttpClientFactory okHttpClient(RestClientProperties restClientProperties) {
        return new OkHttpClientFactory(restClientProperties);
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
    public RestClientInfoCollector RestClientInfoCollector(RestClientProperties config) {
        return new RestClientInfoCollector(config);
    }
    
    @ConditionalOnMissingClass("io.doodler.discovery.feign.DiscoveryClientLoadBalancerClient")
    @Bean
    public LoadBalancerClient loadBalancerClient(RestClientProperties config) {
    	DefaultLoadBalancerClient loadBalancerClient = new DefaultLoadBalancerClient();
    	loadBalancerClient.setConfig(config);
    	return loadBalancerClient;
    }

    @Bean
    public RestClientCustomizer defaultRestClientCustomizer(Client httpClient,
    		                                                LoadBalancerClient loadBalancerClient,
                                                            RestClientProperties restClientProperties,
                                                            ObjectMapper objectMapper,
                                                            RequestInterceptorContainer mappedRequestInterceptorContainer,
                                                            RestClientInterceptorContainer restClientInterceptorContainer) {
        return new DefaultRestClientCustomizer(new StandardClient(httpClient, loadBalancerClient), restClientProperties, objectMapper,
        		mappedRequestInterceptorContainer, restClientInterceptorContainer);
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

    @ConditionalOnProperty(name = "api.trace.chain.enabled", havingValue = "true", matchIfMissing = true)
    @Bean
    public TraceableRestClientInterceptor traceableRestClientInterceptor() {
        return new TraceableRestClientInterceptor();
    }
}