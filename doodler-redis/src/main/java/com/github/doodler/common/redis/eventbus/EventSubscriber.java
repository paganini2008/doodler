package com.github.doodler.common.redis.eventbus;

/**
 * @Description: EventSubscriber
 * @Author: Fred Feng
 * @Date: 09/01/2020
 * @Version 1.0.0
 */
public interface EventSubscriber {

	default boolean supports(Class<?> eventClass) {
		return true;
	}

	void fireEvent(Object event);
}