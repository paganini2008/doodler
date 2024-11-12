package com.github.doodler.common.amqp;

import static com.github.doodler.common.amqp.AmqpConstants.AMQP_HEADER_EVENT_SENDER;
import static com.github.doodler.common.amqp.AmqpConstants.AMQP_HEADER_EVENT_TYPE;
import static com.github.doodler.common.amqp.AmqpConstants.DEFAULT_DEADLETTER_MESSAGE_EXPIRATION;
import static com.github.doodler.common.amqp.AmqpConstants.DEFAULT_FANOUT_EXCHANGE_NAME;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import com.github.doodler.common.enums.EventType;
import com.github.doodler.common.utils.JacksonUtils;
import com.github.doodler.common.utils.Markers;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: AmqpMessageSender
 * @Author: Fred Feng
 * @Date: 12/01/2023
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class AmqpMessageSender {

    private final AmqpAdminService amqpAdminService;
    private final RabbitTemplate rabbitTemplate;
    private final MessagePostProcessor defaultMessagePostProcessor;
    private final RetryCache retryCache;
    private final AmqpMessageStatistics messageStatistics;

    @Value("${spring.rabbitmq.provider.retryInterval:3000}")
    private long retryInterval;

    @Value("${spring.application.name}")
    private String applicationName;

    @SneakyThrows
    public void send(String exchange, String routingKey, Message message) {
        Assert.notNull(message, "Nullable message object for sending");
        String id = UUID.randomUUID().toString();
        retryCache.putObject(id, new SimpleCachedObject(message).setExchange(exchange).setRoutingKey(routingKey)
                .setRetryAt(System.currentTimeMillis() + retryInterval));
        rabbitTemplate.send(exchange, routingKey, message, new CorrelationData(id));
    }

    @SneakyThrows
    public void convertAndSend(String exchange, String routingKey, Object payload) {
        Assert.notNull(payload, "Nullable message object for sending");
        String id = UUID.randomUUID().toString();
        retryCache.putObject(id, new SimpleCachedObject(payload).setExchange(exchange).setRoutingKey(routingKey)
                .setRetryAt(System.currentTimeMillis() + retryInterval));
        rabbitTemplate.convertAndSend(exchange, routingKey, payload, defaultMessagePostProcessor, new CorrelationData(id));
    }

    @SneakyThrows
    public void convertAndSend(String exchange, String routingKey, Object payload,
                               MessagePostProcessor messagePostProcessor) {
        Assert.notNull(payload, "Nullable message object for sending");
        String id = UUID.randomUUID().toString();
        retryCache.putObject(id, new SimpleCachedObject(payload).setExchange(exchange).setRoutingKey(routingKey)
                .setRetryAt(System.currentTimeMillis() + retryInterval));
        rabbitTemplate.convertAndSend(exchange, routingKey, payload, messagePostProcessor, new CorrelationData(id));
    }

    @SneakyThrows
    public void send(Message message) {
        Assert.notNull(message, "Nullable message object for sending");
        send(DEFAULT_FANOUT_EXCHANGE_NAME, null, message);
    }

    @SneakyThrows
    public void send(EventType eventType, Message message) {
        Assert.notNull(message, "Nullable message object for sending");
        String exchange = null, routingKey = "";
        if (eventType == null) {
            exchange = DEFAULT_FANOUT_EXCHANGE_NAME;
        } else {
            routingKey = eventType.getValue();
            exchange = amqpAdminService.getExchangeByRoutingKey(routingKey);
            if (StringUtils.isBlank(exchange)) {
                exchange = DEFAULT_FANOUT_EXCHANGE_NAME;
            }
        }
        send(exchange, routingKey, message);
    }

    @SneakyThrows
    public void convertAndSend(Object payload) {
        Assert.notNull(payload, "Nullable message object for sending");
        convertAndSend(DEFAULT_FANOUT_EXCHANGE_NAME, null, payload);
    }

    @SneakyThrows
    public void convertAndSend(final EventType eventType, final Object payload) {
        Assert.notNull(payload, "Nullable message object for sending");
        String exchange = null, routingKey = "";
        if (eventType == null) {
            exchange = DEFAULT_FANOUT_EXCHANGE_NAME;
        } else {
            routingKey = eventType.getValue();
            exchange = amqpAdminService.getExchangeByRoutingKey(routingKey);
            if (StringUtils.isBlank(exchange)) {
                exchange = DEFAULT_FANOUT_EXCHANGE_NAME;
            }
        }
        convertAndSend(exchange, routingKey, payload, message -> {
            message.getMessageProperties().setHeader(AMQP_HEADER_EVENT_TYPE, eventType.name());
            message.getMessageProperties().setHeader(AMQP_HEADER_EVENT_SENDER, applicationName);
            message.getMessageProperties().setExpiration(DEFAULT_DEADLETTER_MESSAGE_EXPIRATION);
            messageStatistics.push(eventType.name(), payload);
            if(log.isInfoEnabled()) {
            	log.info(Markers.forName(applicationName),"[Event-Publisher] eventType: {}, eventTypeName: {}, payload: {}", eventType, eventType.name(), JacksonUtils.toJsonString(payload));
            }
            return defaultMessagePostProcessor.postProcessMessage(message);
        });
    }
}