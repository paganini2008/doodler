package io.doodler.ip;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

/**
 * @Description: GeoCachedKeyGenerator
 * @Author: Fred Feng
 * @Date: 20/04/2023
 * @Version 1.0.0
 */
@Component("geoCacheKeyGenerator")
public class GeoCachedKeyGenerator implements KeyGenerator {
	
	private final static Base64.Encoder encoder = Base64.getEncoder();

    @Override
    public Object generate(Object target, Method method, Object... params) {
        if (ArrayUtils.isEmpty(params)) {
            throw new IllegalArgumentException("Ip must be required");
        }
        String ip = (String) params[0];
        return encoder.encodeToString(ip.getBytes(StandardCharsets.UTF_8));
    }
}