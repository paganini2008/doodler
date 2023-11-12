package io.doodler.common.amqp;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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

    @ConditionalOnMissingBean
    @Bean
    public MessageHistoryCollector messageHistoryCollector() {
        return new MessageHistoryCollector();
    }
}