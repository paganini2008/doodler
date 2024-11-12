package com.elraytech.maxibet.common.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * 
 * @Description: ApplicationContextConfigInitializer
 * @Author: Fred Feng
 * @Date: 30/08/2024
 * @Version 1.0.0
 */
public class ApplicationContextConfigInitializer implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        environment.getRequiredProperty("");
    }

}
