package com.github.doodler.common.redis.pubsub;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @Description: RedisMessageEntity
 * @Author: Fred Feng
 * @Date: 14/12/2022
 * @Version 1.0.0
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class RedisMessageEntity {

    private String channel;
    private PubSubMode mode;
    private Object message;

    public RedisMessageEntity(String channel, PubSubMode mode, Object message) {
        this.channel = channel;
        this.mode = mode;
        this.message = message;
    }
}