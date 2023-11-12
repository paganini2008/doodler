package io.doodler.common.id;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.doodler.common.utils.Env;

/**
 * @Description: IdGeneratorConfig
 * @Author: Fred Feng
 * @Date: 16/11/2022
 * @Version 1.0.0
 */
@Configuration(proxyBeanMethods = false)
public class IdGeneratorConfig {

    @ConditionalOnMissingBean
    @Bean
    public IdGeneratorFactory idGeneratorFactory() {
        final int pid = Env.getPid();
        return new SnowFlakeIdGeneratorFactory(pid & 31, 0);
    }
}