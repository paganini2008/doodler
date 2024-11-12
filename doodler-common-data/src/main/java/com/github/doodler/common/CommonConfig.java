package com.github.doodler.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.doodler.common.context.ContextPath;

/**
 * 
 * @Description: CommonConfig
 * @Author: Fred Feng
 * @Date: 21/10/2024
 * @Version 1.0.0
 */
@Configuration(proxyBeanMethods = false)
public class CommonConfig {

    @Bean
    public ContextPath contextPath() {
        return new ContextPath();
    }

    @Bean
    public ExceptionTransformer exceptionTransferer() {
        return new DefaultExceptionTransformer();
    }

}
