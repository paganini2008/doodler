package com.github.doodler.common.cloud.redis;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * 
 * @Description: ConditionalOnRedisDiscoveryEnabled
 * @Author: Fred Feng
 * @Date: 04/08/2024
 * @Version 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@ConditionalOnProperty(value = "spring.cloud.redis.discovery.enabled", havingValue = "true", matchIfMissing = true)
public @interface ConditionalOnRedisDiscoveryEnabled {
}
