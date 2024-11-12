package com.github.doodler.common.amqp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;

/**
 * @Description: DefaultMessagePostProcessor
 * @Author: Fred Feng
 * @Date: 15/01/2023
 * @Version 1.0.0
 */
@Slf4j
public class DefaultMessagePostProcessor implements MessagePostProcessor {

    @Override
    public Message postProcessMessage(Message message) throws AmqpException {
        if (log.isDebugEnabled()) {
            log.debug("+----------------------------------------------------------------+");
            log.debug("[    CorrelationId]: {}", message.getMessageProperties().getCorrelationId());
            log.debug("[      DeliveryTag]: {}", message.getMessageProperties().getDeliveryTag());
            log.debug("[MessageProperties]: {}", message.getMessageProperties().toString());
            log.debug("+----------------------------------------------------------------+");
        }
        return message;
    }
}