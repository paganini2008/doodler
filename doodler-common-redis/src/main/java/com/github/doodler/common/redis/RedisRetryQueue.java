package com.github.doodler.common.redis;

import java.util.Collection;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import com.github.doodler.common.retry.RetryQueue;
import lombok.RequiredArgsConstructor;

/**
 * @Description: RedisRetryQueue
 * @Author: Fred Feng
 * @Date: 18/02/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class RedisRetryQueue implements RetryQueue {

	private final String key;
	private final RedisTemplate<String, Object> redisTemplate;

	@Override
	public void putObject(Object object) {
		redisTemplate.opsForList().leftPush(key, object);
	}

	@Override
	public void removeObject(Object object) {
		redisTemplate.opsForList().remove(key, 1, object);
	}

	@Override
	public int size() {
		Long result = redisTemplate.opsForList().size(key);
		return result != null ? result.intValue() : 0;
	}

	@Override
	public void drainTo(Collection<Object> output) {
		final int size = size();
		if (size > 0) {
			List<Object> values = redisTemplate.opsForList().range(key, 0, size);
			output.addAll(values);
			redisTemplate.opsForList().trim(key, 0, size);
		}
	}
}