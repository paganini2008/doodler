package com.github.doodler.common.ws;

import java.util.function.ToDoubleFunction;

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
 * @Description: WsMetricsCollector
 * @Author: Fred Feng
 * @Date: 18/02/2023
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class WsMetricsCollector implements MetricsCollector {

	private final OnlineNumberAccumulator onlineNumberAccumulator;
	private final MeterRegistry registry;

	@Value("${spring.profiles.active}")
	private String env;

	@Value("${server.port}")
	private int port;

	private final String localHost = NetUtil.getLocalhostStr();

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() throws Exception {
        refreshMetrics();
        if (log.isTraceEnabled()) {
            log.trace("Start to collect metrics and push to MeterRegistry ...");
        }
    }
	
	@Override
	public void refreshMetrics() throws Exception{
        createGauge("website.online.number", "Online number of website",
        		OnlineNumberAccumulator::onlineNumberOfWebsite);
        createGauge("user.online.number", "Online number of users",
        		OnlineNumberAccumulator::onlineNumberOfUsers);
        createGauge("chat.online.number", "Online number of chat room",
        		OnlineNumberAccumulator::onlineNumberOfChat);
	}
	
    private void createGauge(String metric, String help, ToDoubleFunction<OnlineNumberAccumulator> measure) {
        Gauge.builder(metric, onlineNumberAccumulator, measure)
                .description(help)
                .tag("env", env)
                .tag("instance", localHost + ":" + port)
                .register(this.registry);
    }
}