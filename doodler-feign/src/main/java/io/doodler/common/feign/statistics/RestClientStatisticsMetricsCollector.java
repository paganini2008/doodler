package io.doodler.common.feign.statistics;

import java.util.function.ToDoubleFunction;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import cn.hutool.core.net.NetUtil;
import io.doodler.common.context.MetricsCollector;
import io.doodler.common.feign.RestClientMetadataCollector;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: RestClientStatisticsMetricsCollector
 * @Author: Fred Feng
 * @Date: 06/03/2023
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class RestClientStatisticsMetricsCollector implements MetricsCollector {

    private final MeterRegistry registry;
    private final RestClientMetadataCollector restClientMetadataCollector;
    private final RestClientStatisticsService restClientStatisticsService;

    @Value("${spring.profiles.active}")
    private String env;

    @Value("${server.port}")
    private int port;

    private final String localHost = NetUtil.getLocalhostStr();

    @Override
    public void refreshMetrics() throws Exception {
        restClientMetadataCollector.metadatas().forEach(info -> {
            String applicationName = info.getServiceId();

            createGauge(restClientStatisticsService,
                    "rest.client.execution.success.percent",
                    "Rest client execution success percent",
                    applicationName,
                    statisticsService -> statisticsService.sampler("all", applicationName).getSample().getSuccessPercent());

            createGauge(restClientStatisticsService,
                    "rest.client.execution.slow.percent",
                    "Rest client execution slow percent",
                    applicationName,
                    statisticsService -> statisticsService.sampler("all", applicationName).getSample().getSlowPercent());

            createGauge(restClientStatisticsService,
                    "rest.client.execution.average.time",
                    "Rest client execution average time",
                    applicationName,
                    statisticsService -> statisticsService.sampler("all", applicationName).getSample().getAverageExecutionTime());

            createGauge(restClientStatisticsService,
                    "rest.client.execution.average.tps",
                    "Rest client execution average tps",
                    applicationName,
                    statisticsService -> statisticsService.sampler("all", applicationName).getSample().getTps());
            
            createGauge(restClientStatisticsService,
                    "rest.client.execution.api.concurrents",
                    "Rest client api concurrents",
                    applicationName,
                    statisticsService -> statisticsService.sampler("all", applicationName).getSample().getConcurrentCount());
        });
    }

    private void createGauge(RestClientStatisticsService weakRef, String metric, String help, String applicationName,
                             ToDoubleFunction<RestClientStatisticsService> measure) {
        Gauge.builder(metric, weakRef, measure)
                .description(help)
                .tag("env", env)
                .tag("instance", localHost + ":" + port)
                .tag("service_id", applicationName)
                .register(this.registry);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() throws Exception {
        refreshMetrics();
        if (log.isTraceEnabled()) {
            log.trace("Start to collect metrics and push to MeterRegistry ...");
        }
    }
}