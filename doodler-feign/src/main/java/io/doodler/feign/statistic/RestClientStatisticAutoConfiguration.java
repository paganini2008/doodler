package io.doodler.feign.statistic;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.fasterxml.jackson.databind.ObjectMapper;

import feign.Client;
import io.doodler.feign.LoadBalancerClient;
import io.doodler.feign.RequestInterceptorContainer;
import io.doodler.feign.RestClientCandidatesAutoConfiguration;
import io.doodler.feign.RestClientCustomizer;
import io.doodler.feign.RestClientInterceptorContainer;
import io.doodler.feign.RestClientProperties;
import io.doodler.feign.RestClientUtils;
import io.doodler.feign.StandardClient;
import io.doodler.feign.actuator.RestClientStatisticsEndpoint;

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

    @ConditionalOnProperty(name = "feign.client.statistic.type", havingValue = "concise", matchIfMissing = true)
    public static class ConciseRestClientStatisticsCollectorConfig {

        @Bean
        public AbstractRestClientStatisticsCollector conciseRestClientStatisticsCollector() {
            return new ConciseRestClientStatisticsCollector();
        }

        @Primary
        @Bean
        public RestClientCustomizer conciseStatisticalRestClientCustomizer(Client httpClient,
        		                                                          LoadBalancerClient loadBalancerClient,
                                                                          RestClientProperties restClientProperties,
                                                                          ObjectMapper objectMapper,
                                                                          RequestInterceptorContainer mappedRequestInterceptorContainer,
                                                                          RestClientInterceptorContainer restClientInterceptorContainer) {
            return new ConciseStatisticalRestClientCustomizer(new StandardClient(httpClient, loadBalancerClient), restClientProperties, 
            		objectMapper, mappedRequestInterceptorContainer, restClientInterceptorContainer);
        }
    }

    @ConditionalOnProperty(name = "feign.client.statistic.type", havingValue = "exact")
    public static class ExactRestClientStatisticsCollectorConfig {

        @Bean
        public AbstractRestClientStatisticsCollector exactClientStatisticsCollector() {
            return new ExactRestClientStatisticsCollector();
        }

        @Primary
        @Bean
        public RestClientCustomizer exactStatisticalRestClientCustomizer(Client httpClient,
        		                                                        LoadBalancerClient loadBalancerClient,
                                                                        RestClientProperties restClientProperties,
                                                                        ObjectMapper objectMapper,
                                                                        RequestInterceptorContainer mappedRequestInterceptorContainer,
                                                                        RestClientInterceptorContainer restClientInterceptorContainer,
                                                                        AbstractRestClientStatisticsCollector restClientStatisticsCollector) {
            return new ExactStatisticalRestClientCustomizer(new StandardClient(httpClient, loadBalancerClient), restClientProperties, objectMapper,
            		mappedRequestInterceptorContainer, restClientInterceptorContainer, restClientStatisticsCollector);
        }
    }
}