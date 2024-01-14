package com.github.doodler.common.amqp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @Description: AmqpSharedConfig
 * @Author: Fred Feng
 * @Date: 13/04/2023
 * @Version 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@Import({MessageHistoryEndpoint.class})
public class AmqpSharedConfig {

    @Bean
    public AmqpMessageStatistics amqpMessageStatistics() {
        return new AmqpMessageStatistics();
    }

    @Bean
    public AmqpMessageSenderHealthIndicator amqpMessageSenderHealthIndicator(AmqpMessageStatistics amqpMessageStatistics) {
        return new AmqpMessageSenderHealthIndicator(amqpMessageStatistics);
    }
}