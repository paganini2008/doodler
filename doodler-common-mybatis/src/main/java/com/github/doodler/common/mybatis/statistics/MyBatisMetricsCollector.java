package com.github.doodler.common.mybatis.statistics;

import java.util.function.ToDoubleFunction;

import org.apache.ibatis.mapping.SqlCommandType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import com.github.doodler.common.context.MetricsCollector;
import cn.hutool.core.net.NetUtil;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: MyBatisMetricsCollector
 * @Author: Fred Feng
 * @Date: 25/09/2023
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class MyBatisMetricsCollector implements MetricsCollector {

    private final MeterRegistry registry;
    private final MyBatisStatisticsService statisticsService;

    @Value("${spring.profiles.active}")
    private String env;

    @Value("${server.port}")
    private int port;

    private final String localHost = NetUtil.getLocalhostStr();

    @Override
    public void refreshMetrics() throws Exception {
        for (SqlCommandType sqlCommandType : SqlCommandType.values()) {
            if (sqlCommandType == SqlCommandType.INSERT || sqlCommandType == SqlCommandType.UPDATE
                    || sqlCommandType == SqlCommandType.DELETE || sqlCommandType == SqlCommandType.SELECT) {
                String operatorType = sqlCommandType.name().toLowerCase();
                
                createGauge(statisticsService, "sql.command.execution.success.percent", "Sql command execution success percent",
                        operatorType,
                        statisticsService -> statisticsService.sampler("sql_command", operatorType).getSample()
                                .getSuccessPercent());

                createGauge(statisticsService, "sql.command.execution.slow.percent", "Sql command execution slow percent",
                        operatorType,
                        statisticsService -> statisticsService.sampler("sql_command", operatorType).getSample()
                                .getSlowPercent());

                createGauge(statisticsService, "sql.command.execution.average.time", "Sql command average execution time",
                        operatorType,
                        statisticsService -> statisticsService.sampler("sql_command", operatorType).getSample()
                                .getAverageExecutionTime());
            }
        }
    }

    private void createGauge(MyBatisStatisticsService ref,
                             String metric,
                             String help,
                             String operatorType,
                             ToDoubleFunction<MyBatisStatisticsService> measure) {
        Gauge.builder(metric, ref, measure)
                .description(help)
                .tag("env", env)
                .tag("instance", localHost + ":" + port)
                .tag("operator_type", operatorType)
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