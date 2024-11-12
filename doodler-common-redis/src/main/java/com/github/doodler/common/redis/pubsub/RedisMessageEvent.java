package com.github.doodler.common.redis.pubsub;

import org.springframework.context.ApplicationEvent;

/**
 * 
 * @Description: RedisMessageEvent
 * @Author: Fred Feng
 * @Date: 14/12/2022
 * @Version 1.0.0
 */
public class RedisMessageEvent extends ApplicationEvent {

    private static final long serialVersionUID = -1095142634067415031L;

    public RedisMessageEvent(Object source, String channel, Object message) {
        super(source);
        this.channel = channel;
        this.message = message;
    }

    private final String channel;
    private final Object message;

    public String getChannel() {
        return channel;
    }

    public Object getMessage() {
        return message;
    }
}