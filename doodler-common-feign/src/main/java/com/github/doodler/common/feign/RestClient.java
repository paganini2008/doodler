package com.github.doodler.common.feign;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @Description: RestClient
 * @Author: Fred Feng
 * @Date: 28/11/2022
 * @Version 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface RestClient {

    String serviceId() default "";

    String url() default "";

    int retries() default 3;

    long connectionTimeout() default 10;

    long readTimeout() default 60;
    
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    boolean followRedirects() default true;
    
    Class<?> fallback() default void.class;
    
    Class<?> fallbackFactory() default void.class;
    
}