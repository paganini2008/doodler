package id.doodler.ws;

import java.util.function.ToDoubleFunction;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import cn.hutool.core.net.NetUtil;
import io.doodler.common.context.MetricsCollector;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;

/**
 * @Description: WsMetricsCollector
 * @Author: Fred Feng
 * @Date: 18/02/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class WsMetricsCollector implements InitializingBean, MetricsCollector {

	private final OnlineNumberAccumulator onlineNumberAccumulator;
	private final MeterRegistry registry;

	@Value("${spring.profiles.active}")
	private String env;

	@Value("${server.port}")
	private int port;

	private final String localHost = NetUtil.getLocalhostStr();

	@Override
	public void afterPropertiesSet() throws Exception {
		refreshMetrics();
	}
	
	@Override
	public void refreshMetrics() throws Exception{
        createGauge("website_online_number", "Online number of website",
        		OnlineNumberAccumulator::onlineNumberOfWebsite);
        createGauge("user_online_number", "Online number of users",
        		OnlineNumberAccumulator::onlineNumberOfUsers);
        createGauge("chat_online_number", "Online number of chat room",
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