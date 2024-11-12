package com.github.doodler.common.redis;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.KeyspaceEventMessageListener;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.lang.Nullable;

/**
 * @Description: RedisKeyDeleteEventMessageListener
 * @Author: Fred Feng
 * @Date: 24/05/2023
 * @Version 1.0.0
 * @see KeyExpirationEventMessageListener
 */
public class RedisKeyDeleteEventMessageListener extends KeyspaceEventMessageListener implements
		ApplicationEventPublisherAware {

	private static final Topic KEYEVENT_DELETE_TOPIC = new PatternTopic("__keyevent@*__:del");

	private @Nullable ApplicationEventPublisher publisher;

	public RedisKeyDeleteEventMessageListener(RedisMessageListenerContainer listenerContainer) {
		super(listenerContainer);
	}

	protected void doRegister(RedisMessageListenerContainer listenerContainer) {
		listenerContainer.addMessageListener(this, KEYEVENT_DELETE_TOPIC);
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.publisher = applicationEventPublisher;
	}

	@Override
	protected void doHandleMessage(Message message) {
		this.publishEvent(new RedisKeyDeleteEvent(message.getBody()));
	}

	protected void publishEvent(RedisKeyDeleteEvent event) {
		if (publisher != null) {
			this.publisher.publishEvent(event);
		}
	}
}