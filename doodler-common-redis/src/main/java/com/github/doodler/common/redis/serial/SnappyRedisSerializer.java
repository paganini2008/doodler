package com.github.doodler.common.redis.serial;

import java.io.Serializable;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.xerial.snappy.Snappy;

/**
 * @Description: SnappyRedisSerializer
 * @Author: Fred Feng
 * @Date: 05/01/2023
 * @Version 1.0.0
 */
public class SnappyRedisSerializer<T> implements RedisSerializer<T> {

    private final RedisSerializer<T> delegate;
    
    public SnappyRedisSerializer() {
    	this(null);
    }

    public SnappyRedisSerializer(RedisSerializer<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public byte[] serialize(T obj) throws SerializationException {
    	 if (obj == null) {
             return ArrayUtils.EMPTY_BYTE_ARRAY;
         }
        try {
            byte[] bytes = delegate != null ? delegate.serialize(obj)
                    : SerializationUtils.serialize((Serializable) obj);
            return Snappy.compress(bytes);
        } catch (Exception e) {
            throw new SerializationException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            byte[] bos = Snappy.uncompress(bytes);
            return (T) (delegate != null ?
                    delegate.deserialize(bos) : SerializationUtils.deserialize(bos));
        } catch (Exception e) {
            throw new SerializationException(e.getMessage(), e);
        }
    }
}