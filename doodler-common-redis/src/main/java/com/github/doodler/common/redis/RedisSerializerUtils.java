package com.github.doodler.common.redis;

import static com.github.doodler.common.Constants.ISO8601_DATE_TIME_PATTERN;
import java.text.SimpleDateFormat;

import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.doodler.common.redis.serial.KryoRedisSerializer;
import com.github.doodler.common.redis.serial.SnappyRedisSerializer;
import com.github.doodler.common.utils.JacksonUtils;
import lombok.experimental.UtilityClass;

/**
 * @Description: RedisSerializerUtils
 * @Author: Fred Feng
 * @Date: 10/01/2023
 * @Version 1.0.0
 */
@UtilityClass
public class RedisSerializerUtils {
	
	public RedisSerializer<Object> chooseRedisSerializer(String type){
		switch (type) {
		case "jackson":
			return getJacksonRedisSerializer();
		case "kryo":
			return new KryoRedisSerializer<>(Object.class);
		case "snappy":
			return getSnappyRedisSerializer();
		case "java":
			return RedisSerializer.java();
		default:
			throw new UnsupportedOperationException("Unknown redis serializer type: " + type);
		}
	}

    public Jackson2JsonRedisSerializer<Object> getJacksonRedisSerializer() {
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        om.setDateFormat(new SimpleDateFormat(ISO8601_DATE_TIME_PATTERN));
        SimpleModule javaTimeModule = JacksonUtils.getJavaTimeModuleForWebMvc();
        om.registerModule(javaTimeModule);
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(
                Object.class);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        return jackson2JsonRedisSerializer;
    }

    public SnappyRedisSerializer<Object> getSnappyRedisSerializer() {
        return getSnappyRedisSerializer(new KryoRedisSerializer<>(Object.class));
    }

    public SnappyRedisSerializer<Object> getSnappyRedisSerializer(RedisSerializer<Object> delegate) {
        return new SnappyRedisSerializer<>(delegate);
    }
}