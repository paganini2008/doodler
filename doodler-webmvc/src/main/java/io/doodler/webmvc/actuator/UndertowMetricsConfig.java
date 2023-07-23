package io.doodler.webmvc.actuator;

import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: UndertowMetricsConfig
 * @Author: Fred Feng
 * @Date: 16/07/2023
 * @Version 1.0.0
 */
@Configuration(proxyBeanMethods = false)
public class UndertowMetricsConfig {
	
	@Bean
	public UndertowMetricsHandlerWrapper undertowMetricsHandlerWrapper() {
		return new UndertowMetricsHandlerWrapper();
	}

    @Bean
    public UndertowDeploymentInfoCustomizer undertowDeploymentInfoCustomizer(
            UndertowMetricsHandlerWrapper undertowMetricsHandlerWrapper) {
        return deploymentInfo ->
                deploymentInfo.addOuterHandlerChainWrapper(undertowMetricsHandlerWrapper);
    }

    @Bean
    public UndertowMeterBinder undertowMeterBinder(UndertowMetricsHandlerWrapper undertowMetricsHandlerWrapper) {
        return new UndertowMeterBinder(undertowMetricsHandlerWrapper);
    }
}