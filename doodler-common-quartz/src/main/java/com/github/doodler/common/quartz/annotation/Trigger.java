package com.github.doodler.common.quartz.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @Description: Trigger
 * @Author: Fred Feng
 * @Date: 13/06/2023
 * @Version 1.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Trigger {

	String name() default "";

	String group() default "";
	
	String description() default "";

	String cron() default "";

	long initialDelay() default -1L;

	long period() default -1L;

	TimeUnit timeUnit() default TimeUnit.SECONDS;

	int repeatCount() default -1;
	
    boolean update() default false;
}