package io.doodler.feign.statistics;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import feign.Client;
import io.doodler.feign.EncoderDecoderFactory;
import io.doodler.feign.LoadBalancerClient;
import io.doodler.feign.RequestInterceptorContainer;
import io.doodler.feign.RestClientCandidatesAutoConfiguration;
import io.doodler.feign.RestClientCustomizer;
import io.doodler.feign.RestClientInterceptorContainer;
import io.doodler.feign.RestClientMetadataCollector;
import io.doodler.feign.RestClientProperties;
import io.doodler.feign.RestClientUtils;
import io.doodler.feign.RetryFailureHandlerContainer;
import io.doodler.feign.StandardClient;
import io.doodler.feign.actuator.RestClientStatisticsHealthIndicator;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * @Description: RestClientStatisticAutoConfiguration
 * @Author: Fred Feng
 * @Date: 29/01/2023
 * @Version 1.0.0
 */
@ConditionalOnProperty(name = "feign.client.statistic.enabled", havingValue = "true", matchIfMissing = true)
@AutoConfigureAfter(RestClientCandidatesAutoConfiguration.class)
@Configuration(proxyBeanMethods = false)
public class RestClientStatisticAutoConfiguration {

    @Bean
    public LatestRequestHistoryCollector latestRequestHistoryCollector() {
    	LatestRequestHistoryCollector latestRequestHistoryCollector = new LatestRequestHistoryCollector();
    	RestClientUtils.addRestClientInterceptor(latestRequestHistoryCollector);
    	return latestRequestHistoryCollector;
    }

    @Bean
    public RestClientStatisticsEndpoint restClientStatisticsEndpoint() {
        return new RestClientStatisticsEndpoint();
    }
    
    @Bean
    public RestClientStatisticsService restClientStatisticsService() {
    	return new RestClientStatisticsService();
    }
    
    @ConditionalOnProperty(name = "management.health.restClientStatistics.enabled", havingValue = "true", matchIfMissing = true)
    @Bean
    public RestClientStatisticsHealthIndicator restClientStatisticsHealthIndicator(RestClientStatisticsService statisticsService) {
    	return new RestClientStatisticsHealthIndicator(statisticsService);
    }
    
    @Bean
    public RestClientStatisticsMetricsCollector restClientMetricsCollector(MeterRegistry registry, 
    		RestClientMetadataCollector restClientInfoCollector,
    		RestClientStatisticsService restClientStatisticsService) {
    	return new RestClientStatisticsMetricsCollector(registry, restClientInfoCollector, restClientStatisticsService);
    }
    
    @Bean
    public StatisticalRetryFailureHandler statisticalRetryFailureHandler(RestClientStatisticsService statisticsService) {
    	return new StatisticalRetryFailureHandler(statisticsService);
    }

    @ConditionalOnProperty(name = "feign.client.statistic.type", havingValue = "by-method", matchIfMissing = true)
    public static class PerMethodStatisticsCollectorConfig {

        @Bean
        public PerMethodStatisticsCollector perMethodStatisticsCollector(RestClientMetadataCollector restClientMetadataCollector, RestClientStatisticsService statisticsService) {
        	return new PerMethodStatisticsCollector(restClientMetadataCollector, statisticsService);
        }

        @Primary
        @Bean
        public RestClientCustomizer perMethodStatisticalRestClientCustomizer(Client httpClient,
        		                                                          EncoderDecoderFactory encoderDecoderFactory,
        		                                                          LoadBalancerClient loadBalancerClient,
                                                                          RestClientProperties restClientProperties,
                                                                          RequestInterceptorContainer mappedRequestInterceptorContainer,
                                                                          RestClientInterceptorContainer restClientInterceptorContainer,
                                                                          RetryFailureHandlerContainer retryFailureHandlerContainer) {
            return new PerMethodStatisticalRestClientCustomizer(new StandardClient(httpClient, loadBalancerClient),
            		encoderDecoderFactory, 
            		restClientProperties, 
            		mappedRequestInterceptorContainer,
            		restClientInterceptorContainer,
            		retryFailureHandlerContainer);
        }
    }

    @ConditionalOnProperty(name = "feign.client.statistic.type", havingValue = "by-request")
    public static class PerRequestStatisticsCollectorConfig {

        @Bean
        public PerRequestStatisticsCollector perRequestStatisticsCollector(RestClientStatisticsService statisticsService) {
        	return new PerRequestStatisticsCollector(statisticsService);
        }

        @Primary
        @Bean
        public RestClientCustomizer perRequestStatisticalRestClientCustomizer(Client httpClient,
        		                                                        EncoderDecoderFactory encoderDecoderFactory,
        		                                                        LoadBalancerClient loadBalancerClient,
                                                                        RestClientProperties restClientProperties,
                                                                        RequestInterceptorContainer mappedRequestInterceptorContainer,
                                                                        RestClientInterceptorContainer restClientInterceptorContainer,
                                                                        RestClientStatisticsService statisticsService,
                                                                        RetryFailureHandlerContainer retryFailureHandlerContainer) {
            return new PerRequestStatisticalRestClientCustomizer(
            		new StandardClient(httpClient, loadBalancerClient), 
            		encoderDecoderFactory, 
            		restClientProperties,
            		mappedRequestInterceptorContainer, 
            		restClientInterceptorContainer, 
            		statisticsService,
            		retryFailureHandlerContainer);
        }
    }
}