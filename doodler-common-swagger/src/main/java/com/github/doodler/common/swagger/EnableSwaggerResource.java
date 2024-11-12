package com.github.doodler.common.swagger;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * @Description: EnableSwaggerResource
 * @Author: Fred Feng
 * @Date: 15/11/2022
 * @Version 1.0.0
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE})
@Documented
@Import(SwaggerResourceConfig.class)
public @interface EnableSwaggerResource {
 
}