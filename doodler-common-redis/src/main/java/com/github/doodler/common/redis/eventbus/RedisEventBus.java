package com.github.doodler.common.redis.eventbus;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import com.github.doodler.common.redis.pubsub.RedisPubSubService;
import lombok.RequiredArgsConstructor;

/**
 * @Description: RedisEventBus
 * @Author: Fred Feng
 * @Date: 09/03/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class RedisEventBus {

    public static final String PUBSUB_CHANNEL_EVENT_BUS = "EVENT_BUS";

    private final RedisPubSubService pubSubService;
    private final List<EventSubscriber> subscribers = new CopyOnWriteArrayList<>();

    public void register(EventSubscriber subscriber) {
        if (subscriber != null) {
            subscribers.add(subscriber);
        }
    }

    public void publish(Object event) {
        pubSubService.convertAndMulticast(PUBSUB_CHANNEL_EVENT_BUS, event);
    }

    public void publish(Object event, long delay, TimeUnit timeUnit) {
        pubSubService.convertAndMulticast(PUBSUB_CHANNEL_EVENT_BUS, event, delay, timeUnit);
    }

    List<EventSubscriber> getSubscribers() {
        return subscribers;
    }
}