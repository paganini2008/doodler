package com.github.doodler.common.context;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Conditional;

/**
 * 
 * @Description: ConditionalOnPropertyPrefix
 * @Author: Fred Feng
 * @Date: 30/11/2024
 * @Version 1.0.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnPropertyPrefixCondition.class)
public @interface ConditionalOnPropertyPrefix {

    String value();

}
