package com.github.doodler.common.redis;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.support.atomic.RedisAtomicDouble;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import lombok.experimental.UtilityClass;

/**
 * @Description: RedisCounterUtils
 * @Author: Fred Feng
 * @Date: 17/02/2023
 * @Version 1.0.0
 */
@UtilityClass
public class RedisCounterUtils {

	public RedisAtomicLong getRedisLongCounter(String counterName, RedisConnectionFactory redisConnectionFactory) {
		return new RedisAtomicLong(counterName, redisConnectionFactory);
	}

	public RedisAtomicLong getRedisLongCounter(String counterName, RedisConnectionFactory redisConnectionFactory,
	                                           Long initialValue) {
		return new RedisAtomicLong(counterName, redisConnectionFactory, initialValue);
	}

	public RedisAtomicInteger getRedisIntegerCounter(String counterName, RedisConnectionFactory redisConnectionFactory) {
		return new RedisAtomicInteger(counterName, redisConnectionFactory);
	}

	public RedisAtomicInteger getRedisIntegerCounter(String counterName, RedisConnectionFactory redisConnectionFactory,
	                                                 Integer initialValue) {
		return new RedisAtomicInteger(counterName, redisConnectionFactory, initialValue);
	}

	public RedisAtomicDouble getRedisDoubleCounter(String counterName, RedisConnectionFactory redisConnectionFactory) {
		return new RedisAtomicDouble(counterName, redisConnectionFactory);
	}

	public RedisAtomicDouble getRedisDoubleCounter(String counterName, RedisConnectionFactory redisConnectionFactory,
	                                               Double initialValue) {
		return new RedisAtomicDouble(counterName, redisConnectionFactory, initialValue);
	}
}