package com.github.doodler.common.redis.pubsub;

import java.util.concurrent.TimeUnit;

/**
 * 
 * @Description: RedisPubSubService
 * @Author: Fred Feng
 * @Date: 14/12/2022
 * @Version 1.0.0
 */
public interface RedisPubSubService {

    void convertAndUnicast(String channel, Object message);

    void convertAndMulticast(String channel, Object message);

    void convertAndUnicast(String channel, Object message, long delay, TimeUnit timeUnit);

    void convertAndMulticast(String channel, Object message, long delay, TimeUnit timeUnit);
}