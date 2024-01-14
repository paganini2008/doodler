package com.github.doodler.common.redis;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.data.redis.core.RedisKeyspaceEvent;
import org.springframework.data.redis.core.convert.MappingRedisConverter.BinaryKeyspaceIdentifier;
import org.springframework.lang.Nullable;

/**
 * @Description: RedisKeyDeleteEvent
 * @Author: Fred Feng
 * @Date: 24/05/2023
 * @Version 1.0.0
 * @see RedisKeyExpiredEvent
 */
public class RedisKeyDeleteEvent extends RedisKeyspaceEvent {

	private static final long serialVersionUID = -4768065818072601376L;

	private static final Charset CHARSET = StandardCharsets.UTF_8;

	private final BinaryKeyspaceIdentifier objectId;
	private final @Nullable Object value;

	public RedisKeyDeleteEvent(byte[] key) {
		this(key, null);
	}

	public RedisKeyDeleteEvent(byte[] key, @Nullable Object value) {
		this(null, key, value);
	}

	public RedisKeyDeleteEvent(@Nullable String channel, byte[] key, @Nullable Object value) {
		super(channel, key);

		if (BinaryKeyspaceIdentifier.isValid(key)) {
			this.objectId = BinaryKeyspaceIdentifier.of(key);
		} else {
			this.objectId = null;
		}

		this.value = value;
	}

	public String getKeyspace() {
		return objectId != null ? new String(objectId.getKeyspace(), CHARSET) : null;
	}

	public byte[] getId() {
		return objectId != null ? objectId.getId() : getSource();
	}

	@Nullable
	public Object getValue() {
		return value;
	}

	@Override
	public String toString() {

		byte[] id = getId();
		return "RedisKeyExpiredEvent [keyspace=" + getKeyspace() + ", id=" + (id == null ? null : new String(id)) + "]";
	}
}