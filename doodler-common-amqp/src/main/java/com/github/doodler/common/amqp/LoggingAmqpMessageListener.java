package com.github.doodler.common.amqp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

/**
 * @Description: LoggingAmqpMessageListener
 * @Author: Fred Feng
 * @Date: 12/01/2023
 * @Version 1.0.0
 */
@Slf4j
public class LoggingAmqpMessageListener extends AmqpMessageListenerSupport {

    public LoggingAmqpMessageListener(Jackson2JsonMessageConverter messageConverter) {
        super(messageConverter);
    }

    @Override
    protected void onMessage(Object payload, MessageProperties messageProperties) {
        if (log.isTraceEnabled()) {
            log.trace("{}", payload);
        }
    }
}