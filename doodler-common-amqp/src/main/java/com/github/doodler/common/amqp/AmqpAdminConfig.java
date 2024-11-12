package com.github.doodler.common.amqp;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: AmqpAdminConfig
 * @Author: Fred Feng
 * @Date: 16/01/2023
 * @Version 1.0.0
 */
@EnableConfigurationProperties({AmqpAdminProperties.class})
@Configuration(proxyBeanMethods = false)
public class AmqpAdminConfig {

    @Bean
    public AmqpAdminService amqpAdminService(AmqpAdmin amqpAdmin, AmqpAdminProperties amqpAdminProperties) {
        return new AmqpAdminService(amqpAdmin, amqpAdminProperties);
    }
}