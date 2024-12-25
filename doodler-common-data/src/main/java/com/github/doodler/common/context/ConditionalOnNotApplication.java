package com.github.doodler.common.context;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Conditional;

/**
 * @Description: ConditionalOnNotApplication
 * @Author: Fred Feng
 * @Date: 08/12/2022
 * @Version 1.0.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnNotApplicationCondition.class)
public @interface ConditionalOnNotApplication {

    String[] value();

}
