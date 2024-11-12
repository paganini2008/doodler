package com.github.doodler.common.amqp;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * @Description: EnableAmqpProvider
 * @Author: Fred Feng
 * @Date: 13/01/2023
 * @Version 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({AmqpSharedConfig.class, AmqpAdminConfig.class, AmqpProviderConfig.class})
public @interface EnableAmqpProvider {
 
}