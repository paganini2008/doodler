package com.github.doodler.common.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * 
 * @Description: EnableSecureLogin
 * @Author: Fred Feng
 * @Date: 28/10/2024
 * @Version 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(SecureLoginFeatures.class)
public @interface EnableSecureLogin {

    boolean apiKeyEnabled() default false;

    boolean oauth2ClientEnabled() default false;

    boolean superAdminEnabled() default false;

}
