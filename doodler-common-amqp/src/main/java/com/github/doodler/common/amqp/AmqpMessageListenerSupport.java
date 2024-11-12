package com.github.doodler.common.amqp;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

/**
 * @Description: AmqpMessageListenerSupport
 * @Author: Fred Feng
 * @Date: 12/01/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public abstract class AmqpMessageListenerSupport implements ChannelAwareMessageListener {

    private final Jackson2JsonMessageConverter messageConverter;

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        final Object payload = messageConverter.fromMessage(message);
        try {
            onMessage(payload, message.getMessageProperties());
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            throw e;
        }
    }

    protected abstract void onMessage(Object payload, MessageProperties messageProperties) throws Exception;
}