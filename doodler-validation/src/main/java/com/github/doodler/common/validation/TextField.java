package com.github.doodler.common.validation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;

/**
 * @Description: TextField
 * @Author: Fred Feng
 * @Date: 29/03/2020
 * @Version 1.0.0
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {TextFieldValidator.class})
public @interface TextField {

    String message() default "{javax.validation.constraints.TextField.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int min() default 6;

    int max() default -1;

    String regex() default "";

    boolean nullable() default false;
    
    boolean encrypted() default false;
    
    String securityKey() default "";

    /**
     * Defines several {@link NotNull} annotations on the same element.
     *
     * @see javax.validation.constraints.NotNull
     */
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RUNTIME)
    @Documented
    @interface List {

        TextField[] value();
    }
}