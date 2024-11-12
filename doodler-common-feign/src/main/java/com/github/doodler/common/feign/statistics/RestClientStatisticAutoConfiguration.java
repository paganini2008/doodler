package com.github.doodler.common.feign.statistics;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import com.github.doodler.common.feign.EncoderDecoderFactory;
import com.github.doodler.common.feign.RequestInterceptorContainer;
import com.github.doodler.common.feign.RestClientCandidatesAutoConfiguration;
import com.github.doodler.common.feign.RestClientCustomizer;
import com.github.doodler.common.feign.RestClientInterceptorContainer;
import com.github.doodler.common.feign.RestClientMetadataCollector;
import com.github.doodler.common.feign.RestClientProperties;
import com.github.doodler.common.feign.RestClientUtils;
import com.github.doodler.common.feign.RetryFailureHandlerContainer;
import com.github.doodler.common.feign.actuator.RestClientStatisticsHealthIndicator;
import feign.Client;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * @Description: RestClientStatisticAutoConfiguration
 * @Author: Fred Feng
 * @Date: 29/01/2023
 * @Version 1.0.0
 */
@ConditionalOnProperty(name = "feign.client.statistic.enabled", havingValue = "true",
        matchIfMissing = true)
@AutoConfigureAfter(RestClientCandidatesAutoConfiguration.class)
@Configuration(proxyBeanMethods = false)
public class RestClientStatisticAutoConfiguration {

    @Bean
    public LatestRequestHistoryCollector latestRequestHistoryCollector() {
        LatestRequestHistoryCollector latestRequestHistoryCollector =
                new LatestRequestHistoryCollector();
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

    @ConditionalOnProperty(name = "management.health.restClientStatistics.enabled",
            havingValue = "true", matchIfMissing = true)
    @Bean
    public RestClientStatisticsHealthIndicator restClientStatisticsHealthIndicator(
            RestClientStatisticsService statisticsService) {
        return new RestClientStatisticsHealthIndicator(statisticsService);
    }

    @Bean
    public RestClientStatisticsMetricsCollector restClientMetricsCollector(MeterRegistry registry,
            RestClientMetadataCollector restClientInfoCollector,
            RestClientStatisticsService restClientStatisticsService) {
        return new RestClientStatisticsMetricsCollector(registry, restClientInfoCollector,
                restClientStatisticsService);
    }

    @Bean
    public StatisticalRetryFailureHandler statisticalRetryFailureHandler(
            RestClientStatisticsService statisticsService) {
        return new StatisticalRetryFailureHandler(statisticsService);
    }

    @ConditionalOnProperty(name = "feign.client.statistic.type", havingValue = "by-method",
            matchIfMissing = true)
    public static class PerMethodStatisticsCollectorConfig {

        @Bean
        public PerMethodStatisticsCollector perMethodStatisticsCollector(
                RestClientMetadataCollector restClientMetadataCollector,
                RestClientStatisticsService statisticsService) {
            return new PerMethodStatisticsCollector(restClientMetadataCollector, statisticsService);
        }

        @Primary
        @Bean
        public RestClientCustomizer perMethodStatisticalRestClientCustomizer(Client httpClient,
                EncoderDecoderFactory encoderDecoderFactory,
                RestClientProperties restClientProperties,
                RequestInterceptorContainer mappedRequestInterceptorContainer,
                RestClientInterceptorContainer restClientInterceptorContainer,
                RetryFailureHandlerContainer retryFailureHandlerContainer) {
            return new PerMethodStatisticalRestClientCustomizer(httpClient, encoderDecoderFactory,
                    restClientProperties, mappedRequestInterceptorContainer,
                    restClientInterceptorContainer, retryFailureHandlerContainer);
        }
    }

    @ConditionalOnProperty(name = "feign.client.statistic.type", havingValue = "by-request")
    public static class PerRequestStatisticsCollectorConfig {

        @Bean
        public PerRequestStatisticsCollector perRequestStatisticsCollector(
                RestClientStatisticsService statisticsService) {
            return new PerRequestStatisticsCollector(statisticsService);
        }

        @Primary
        @Bean
        public RestClientCustomizer perRequestStatisticalRestClientCustomizer(Client httpClient,
                EncoderDecoderFactory encoderDecoderFactory,
                RestClientProperties restClientProperties,
                RequestInterceptorContainer mappedRequestInterceptorContainer,
                RestClientInterceptorContainer restClientInterceptorContainer,
                RestClientStatisticsService statisticsService,
                RetryFailureHandlerContainer retryFailureHandlerContainer) {
            return new PerRequestStatisticalRestClientCustomizer(httpClient, encoderDecoderFactory,
                    restClientProperties, mappedRequestInterceptorContainer,
                    restClientInterceptorContainer, statisticsService,
                    retryFailureHandlerContainer);
        }
    }
}
