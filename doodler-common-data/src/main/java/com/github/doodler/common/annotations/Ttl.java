package com.github.doodler.common.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description: Ttl
 * @Author: Fred Feng
 * @Date: 30/01/2023
 * @Version 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Ttl {

    long value();

    TtlUnit ttlUnit() default TtlUnit.SECONDS;

    boolean evictOnContextClosed() default false;

    boolean evictOnContextRefreshed() default false;
}