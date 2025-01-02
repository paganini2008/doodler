package com.github.doodler.common.scheduler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * @Description: SchedulingConfig
 * @Author: Fred Feng
 * @Date: 03/01/2025
 * @Version 1.0.0
 */
@Configuration(proxyBeanMethods = false)
public class SchedulingConfig {

    @Bean
    public CrosscuttingScheduling crosscuttingScheduling() {
        return new CrosscuttingScheduling();
    }

}
