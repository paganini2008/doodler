package com.github.doodler.common.ip;

import java.lang.reflect.Method;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.util.DigestUtils;

/**
 * 
 * @Description: GeoCachedKeyGenerator
 * @Author: Fred Feng
 * @Date: 24/12/2024
 * @Version 1.0.0
 */
public class GeoCachedKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        if (ArrayUtils.isEmpty(params)) {
            throw new IllegalArgumentException("Ip must be required");
        }
        String ipAddr = (String) params[0];
        return DigestUtils.md5DigestAsHex(ipAddr.getBytes());
    }
}
