package com.github.doodler.common.redis.serial;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * @Description: KryoRedisSerializer
 * @Author: Fred Feng
 * @Date: 05/01/2023
 * @Version 1.0.0
 */
public class KryoRedisSerializer<T> implements RedisSerializer<T> {

    private static final ThreadLocal<Kryo> KYROS = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    private final Class<T> requiredClass;

    public KryoRedisSerializer(Class<T> requiredClass) {
        this.requiredClass = requiredClass;
    }

    @Override
    public byte[] serialize(T obj) throws SerializationException {
        if (obj == null) {
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }
        Kryo kryo = KYROS.get();
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             Output output = new Output(baos)) {
            kryo.writeClassAndObject(output, obj);
            output.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new SerializationException(e.getMessage(), e);
        } finally {
            KYROS.remove();
        }
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        Kryo kryo = KYROS.get();
        try (Input input = new Input(bytes)) {
            return requiredClass.cast(kryo.readClassAndObject(input));
        } finally {
            KYROS.remove();
        }
    }
}