package com.github.doodler.common.feign;

/**
 * @Description: InitializingRestClientBean
 * @Author: Fred Feng
 * @Date: 01/02/2023
 * @Version 1.0.0
 */
public interface InitializingRestClientBean {

    default void initialize(Object proxy, Class<?> apiInterfaceClass, String beanName) {
    }

    default boolean supports(Class<?> apiInterfaceClass, String beanName) {
        return true;
    }
}