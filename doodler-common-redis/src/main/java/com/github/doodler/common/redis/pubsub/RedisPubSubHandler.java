package com.github.doodler.common.redis.pubsub;

/**
 * @Description: RedisPubSubHandler
 * @Author: Fred Feng
 * @Date: 14/12/2022
 * @Version 1.0.0
 */
public interface RedisPubSubHandler {

	String getChannel();

	void onMessage(String channel, Object message) throws Exception;

	default boolean isRepeatable() {
		return true;
	}
	
	default boolean isPrimary() {
		return true;
	}
}