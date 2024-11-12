package com.github.doodler.common.upms;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description: PermissionPersistences
 * @Author: Fred Feng
 * @Date: 07/11/2023
 * @Version 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PermissionPersistences {

	PermissionPersistence[] value() default {};
}