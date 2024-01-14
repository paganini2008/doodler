package com.github.doodler.common.amqp.eventbus;

import static com.github.doodler.common.amqp.AmqpConstants.AMQP_HEADER_EVENT_SENDER;
import static com.github.doodler.common.amqp.AmqpConstants.AMQP_HEADER_EVENT_TYPE;
import static com.github.doodler.common.amqp.AmqpConstants.AMQP_HEADER_TYPE_NAME;

import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Marker;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

import com.github.doodler.common.amqp.AmqpMessageStatistics;
import com.github.doodler.common.amqp.DlxMessageVo;
import com.github.doodler.common.utils.JacksonUtils;
import com.google.common.eventbus.EventBus;
import com.rabbitmq.client.Channel;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: EventBusAmqpMessageListener
 * @Author: Fred Feng
 * @Date: 13/03/2021
 * @Version 1.0.0
 */
@Slf4j
public class EventBusAmqpMessageListener implements ChannelAwareMessageListener {

    private static final String DLX_QUEUE_NAME = "doodler.queue.deadletter";

    public EventBusAmqpMessageListener(EventBus eventBus,
                                       Jackson2JsonMessageConverter messageConverter,
                                       ClassMapper classMapper,
                                       AmqpMessageStatistics messageStatistics,
                                       Marker marker) {
        this.eventBus = eventBus;
        this.messageConverter = messageConverter;
        this.classMapper = classMapper;
        this.messageStatistics = messageStatistics;
        this.marker = marker;
    }

    private final EventBus eventBus;
    private final Jackson2JsonMessageConverter messageConverter;
    private final ClassMapper classMapper;
    private final AmqpMessageStatistics messageStatistics;
    private final Marker marker;

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        String originalType = message.getMessageProperties().getHeader(AMQP_HEADER_TYPE_NAME);
        if (StringUtils.isNotBlank(originalType)) {
            String toType = classMapper.getToTypeName(originalType);
            if (StringUtils.isNotBlank(toType)) {
                message.getMessageProperties().setHeader(AMQP_HEADER_TYPE_NAME, toType);
                originalType = message.getMessageProperties().getHeader(AMQP_HEADER_TYPE_NAME);
            }
        }

        if (classMapper.hasRegisteredType(originalType)) {
            String consumerQueue = message.getMessageProperties().getConsumerQueue();
            if (DLX_QUEUE_NAME.equals(consumerQueue)) {
                String bodyString = new String(message.getBody(), StandardCharsets.UTF_8);
                EventContext eventContext = EventContext.getContext();
                eventContext.setMessage(message);
                eventContext.setChannel(channel);
                String eventName = message.getMessageProperties().getHeader(AMQP_HEADER_EVENT_TYPE);
                String applicationName = message.getMessageProperties().getHeader(AMQP_HEADER_EVENT_SENDER);
                DlxMessageVo dlxMessageVo = new DlxMessageVo(applicationName, eventName, bodyString, message.getMessageProperties());
                messageStatistics.error(eventName, dlxMessageVo);
                triggerEvent(dlxMessageVo);
            } else if (Message.class.getName().equals(originalType)) {
                EventContext eventContext = EventContext.getContext();
                eventContext.setMessage(message);
                eventContext.setChannel(channel);

                triggerEvent(message);
            } else {
                EventContext eventContext = EventContext.getContext();
                eventContext.setMessage(message);
                eventContext.setChannel(channel);

                Object payload = messageConverter.fromMessage(message);

                // Add history
                String eventTypeName = message.getMessageProperties().getHeader(AMQP_HEADER_EVENT_TYPE);
                if (StringUtils.isBlank(eventTypeName)) {
                    eventTypeName = "Unknown";
                }
                messageStatistics.pull(eventTypeName, payload);

                if (log.isInfoEnabled()) {
                    log.info(marker, "[Event-Receiver] eventType: {}, eventTypeName: {}, payload: {}", originalType,
                            eventTypeName, JacksonUtils.toJsonString(payload));
                }
                triggerEvent(payload);
            }
        } else {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }
    
    protected void triggerEvent(Object payload) {
    	eventBus.post(payload);
	}
}