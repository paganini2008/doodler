package com.github.doodler.common.redis;

import java.util.Iterator;

import org.apache.commons.collections4.IteratorUtils;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

import lombok.experimental.UtilityClass;

/**
 * @Description: RedisIteratorUtils
 * @Author: Fred Feng
 * @Date: 25/02/2023
 * @Version 1.0.0
 */
@UtilityClass
public class RedisIteratorUtils {

	public Iterator<Object> zsetIterator(String key, RedisOperations<String, Object> redisOperations) {
		return zsetIterator(key, ScanOptions.scanOptions().build(), redisOperations);
	}

	public Iterator<Object> zsetIterator(String key, String pattern, int count,
	                                     RedisOperations<String, Object> redisOperations) {
		return zsetIterator(key, ScanOptions.scanOptions().match(pattern).count(count).build(), redisOperations);
	}

	public Iterator<Object> zsetIterator(String key, ScanOptions scanOptions,
	                                     RedisOperations<String, Object> redisOperations) {
		Cursor<TypedTuple<Object>> cursor = redisOperations.opsForZSet().scan(key, scanOptions);
		if (cursor != null) {
			return new Iterator<Object>() {

				@Override
				public boolean hasNext() {
					return cursor.hasNext();
				}

				@Override
				public Object next() {
					return cursor.next().getValue();
				}
			};
		}
		return IteratorUtils.emptyIterator();
	}

	public Iterator<Object> setIterator(String key, RedisOperations<String, Object> redisOperations) {
		return setIterator(key, ScanOptions.scanOptions().build(), redisOperations);
	}

	public Iterator<Object> setIterator(String key, String pattern, int count,
	                                    RedisOperations<String, Object> redisOperations) {
		return setIterator(key, ScanOptions.scanOptions().match(pattern).count(count).build(), redisOperations);
	}

	public Iterator<Object> setIterator(String key, ScanOptions scanOptions,
	                                    RedisOperations<String, Object> redisOperations) {
		final Cursor<Object> cursor = redisOperations.opsForSet().scan(key, scanOptions);
		if (cursor != null) {
			return new Iterator<Object>() {

				@Override
				public boolean hasNext() {
					return cursor.hasNext();
				}

				@Override
				public Object next() {
					return cursor.next();
				}
			};
		}
		return IteratorUtils.emptyIterator();
	}
}