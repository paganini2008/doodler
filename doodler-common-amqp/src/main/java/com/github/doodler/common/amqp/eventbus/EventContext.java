package com.github.doodler.common.amqp.eventbus;

import static com.github.doodler.common.amqp.AmqpConstants.AMQP_HEADER_CONSUMER_MAX_RETRIES;
import static com.github.doodler.common.amqp.AmqpConstants.DEFAULT_MAX_CONSUMER_RETRIES;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.amqp.AmqpIllegalStateException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.github.doodler.common.utils.LruMap;
import com.github.doodler.common.utils.MapUtils;
import com.rabbitmq.client.Channel;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: EventContext
 * @Author: Fred Feng
 * @Date: 15/01/2023
 * @Version 1.0.0
 */
@Slf4j
@Getter
@Setter
public final class EventContext {

    private static final ThreadLocal<EventContext> ttl = TransmittableThreadLocal.withInitial(() -> new EventContext());
    private static final LruMap<String, AtomicInteger> retryTags = new LruMap<>(256);

    private Message message;
    private Channel channel;

    public MessageProperties getMessageProperties() {
        if (message == null) {
            throw new AmqpIllegalStateException(
                    "Please ensure both reveiving data from rabbitmq and handling your business logical in the same thread context.");
        }
        return message.getMessageProperties();
    }

    public void commit() throws IOException {
        channel.basicAck(getMessageProperties().getDeliveryTag(), false);
    }

    public void reject() throws IOException {
        Integer maxRetries = getMessageProperties().getHeader(AMQP_HEADER_CONSUMER_MAX_RETRIES);
        if (maxRetries == null || maxRetries.intValue() <= 0) {
            maxRetries = DEFAULT_MAX_CONSUMER_RETRIES;
        }
        String guid = getMessageProperties().getHeader("guid");
        int retryCount = getRetryCount(guid).getAndIncrement();
        boolean requeue = retryCount < maxRetries.intValue();
        if(!requeue) {
        	getMessageProperties().setHeader("abandonded", true);
        }
        try {
            channel.basicNack(getMessageProperties().getDeliveryTag(), false, requeue);
        } finally {
            if (!requeue) {
                retryTags.remove(guid);
                if (log.isWarnEnabled()) {
                    log.warn("The rejected message of guid '{}' should be discarded/dead-lettered.", guid);
                }
            }
        }
    }

    private AtomicInteger getRetryCount(String guid) {
        return MapUtils.getOrCreate(retryTags, guid, AtomicInteger::new);
    }

    public static EventContext getContext() {
        return ttl.get();
    }

    public static void reset() {
        ttl.remove();
    }
}