package com.github.doodler.common.redis;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;

/**
 * @Description: RedisKeyIterator
 * @Author: Fred Feng
 * @Date: 10/02/2023
 * @Version 1.0.0
 */
public class RedisKeyIterator implements Iterator<String> {

	public RedisKeyIterator(String keyPattern, RedisConnectionFactory redisConnectionFactory) {
		ScanOptions options = ScanOptions.scanOptions().match(keyPattern).build();
		RedisConnection connection = redisConnectionFactory.getConnection();
		this.cursor = connection.scan(options);
	}

	private final Cursor<byte[]> cursor;

	@Override
	public boolean hasNext() {
		return cursor.hasNext();
	}

	@Override
	public String next() {
		return new String(cursor.next(), StandardCharsets.UTF_8);
	}
}