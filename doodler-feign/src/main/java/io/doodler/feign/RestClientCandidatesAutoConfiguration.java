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

import feign.Client;
import io.doodler.feign.actuator.LoadBalancerClientEndpoint;
import io.doodler.feign.actuator.RestClientMetadataEndpoint;
import io.doodler.feign.logger.ElkLoggerAdapter;

/**
 * @Description: RestClientCandidatesAutoConfiguration
 * @Author: Fred Feng
 * @Date: 28/11/2022
 * @Version 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@Import({RestClientMetadataEndpoint.class, 
	     LoadBalancerClientEndpoint.class,
	     RestClientExceptionHandler.class})
@EnableConfigurationProperties({RestClientProperties.class})
public class RestClientCandidatesAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public OkHttpClientFactory okHttpClient(RestClientProperties restClientProperties) {
        return new OkHttpClientFactory(restClientProperties);
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
    public RestClientMetadataCollector RestClientInfoCollector(RestClientProperties config) {
        return new RestClientMetadataCollector(config);
    }
    
    @ConditionalOnMissingClass("com.elraytech.maxibet.common.discovery.feign.DiscoveryClientLoadBalancerClient")
    @Bean
    public LoadBalancerClient loadBalancerClient(RestClientProperties config) {
    	DefaultLoadBalancerClient loadBalancerClient = new DefaultLoadBalancerClient();
    	loadBalancerClient.setConfig(config);
    	return loadBalancerClient;
    }

    @Bean
    public RestClientCustomizer defaultRestClientCustomizer(Client httpClient,
    		                                                EncoderDecoderFactory encoderDecoderFactory,
    		                                                LoadBalancerClient loadBalancerClient,
                                                            RestClientProperties restClientProperties,
                                                            RequestInterceptorContainer requestInterceptorContainer,
                                                            RestClientInterceptorContainer restClientInterceptorContainer,
                                                            RetryFailureHandlerContainer retryFailureHandlerContainer) {
        return new DefaultRestClientCustomizer(new StandardClient(httpClient, loadBalancerClient),
        		encoderDecoderFactory, 
        		restClientProperties,
        		requestInterceptorContainer, 
        		restClientInterceptorContainer,
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

    @ConditionalOnProperty(name = "api.trace.chain.enabled", havingValue = "true", matchIfMissing = true)
    @Bean
    public TraceableRestClientInterceptor traceableRestClientInterceptor() {
        return new TraceableRestClientInterceptor();
    }
}