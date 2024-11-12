package com.github.doodler.common.redis.pubsub;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @Description: RedisPubSub
 * @Author: Fred Feng
 * @Date: 14/12/2022
 * @Version 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisPubSub {

    String value();

    boolean repeatable() default true;

    boolean primary() default true;
}