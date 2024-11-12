package com.github.doodler.common.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Conditional;

/**
 * 
 * @Description: ConditionalOnSecureLogin
 * @Author: Fred Feng
 * @Date: 28/10/2024
 * @Version 1.0.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnSecureLoginCondition.class)
public @interface ConditionalOnSecureLogin {

    String value();

}
