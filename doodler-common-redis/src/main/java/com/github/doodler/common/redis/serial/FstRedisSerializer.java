package com.github.doodler.common.redis.serial;

import org.apache.commons.lang3.ArrayUtils;
import org.nustaq.serialization.FSTConfiguration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * @Description: FstRedisSerializer
 * @Author: Fred Feng
 * @Date: 05/01/2023
 * @Version 1.0.0
 */
public class FstRedisSerializer<T> implements RedisSerializer<T> {

	private final FSTConfiguration configuration = FSTConfiguration.createDefaultConfiguration();
	private final Class<T> requiredType;

	public FstRedisSerializer(Class<T> requiredType) {
		this.requiredType = requiredType;
	}

	@Override
	public byte[] serialize(T t) throws SerializationException {
		if (t == null) {
			return ArrayUtils.EMPTY_BYTE_ARRAY;
		}
		return configuration.asByteArray(t);
	}

	@Override
	public T deserialize(byte[] bytes) throws SerializationException {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		return requiredType.cast(configuration.asObject(bytes));
	}
}