package com.github.doodler.common.id;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * @Description: IdGeneratorConfig
 * @Author: Fred Feng
 * @Date: 07/11/2024
 * @Version 1.0.0
 */
@Configuration(proxyBeanMethods = false)
public class IdGeneratorConfig {

    @ConditionalOnMissingBean
    @Bean
    public IdGeneratorFactory idGeneratorFactory() {
        return new IdGenerators.TimeBasedIdGeneratorFactoryBean();
    }

    @ConditionalOnMissingBean
    @Bean
    public StringIdGeneratorFactory stringIdGeneratorFactory() {
        return new IdGenerators.TimeBasedStringIdGeneratorFactoryBean();
    }


}
