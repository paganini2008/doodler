package com.github.doodler.common.quartz;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * @Description: EnableQuartz
 * @Author: Fred Feng
 * @Date: 15/06/2023
 * @Version 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({QuartzIntegratedSelector.class})
public @interface EnableQuartz {

	Mode value() default Mode.EXECUTOR;

	enum Mode {

		EXECUTOR,

		SCHEDULER;
	}
}