package com.github.doodler.common.redis.eventbus;

import com.github.doodler.common.redis.pubsub.RedisPubSub;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @Description: SubscriberEventPublisher
 * @Author: Fred Feng
 * @Date: 10/03/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class SubscriberEventPublisher {
	
	private final RedisEventBus eventBus;
    
    @RedisPubSub(RedisEventBus.PUBSUB_CHANNEL_EVENT_BUS)
    public synchronized void onEvent(String channel, Object event) {
    	eventBus.getSubscribers().forEach(subscriber -> {
            if (subscriber.supports(event.getClass())) {
                subscriber.fireEvent(event);
            }
        });
    }
	
}
