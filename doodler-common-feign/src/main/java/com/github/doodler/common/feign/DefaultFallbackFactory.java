package com.github.doodler.common.feign;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.springframework.context.ApplicationContext;

/**
 * @Description: SimpleFallbackFactory
 * @Author: Fred Feng
 * @Date: 03/02/2023
 * @Version 1.0.0
 */
@Slf4j
public class DefaultFallbackFactory<API> implements FallbackFactory<API> {

    private final Class<API> fallbackClass;
    private final ApplicationContext applicationContext;

    DefaultFallbackFactory(Class<API> fallbackClass, ApplicationContext applicationContext) {
        this.fallbackClass = fallbackClass;
        this.applicationContext = applicationContext;
    }

    @Override
    public API createFallback(Throwable cause) {
        try {
            return (API) applicationContext.getBean(fallbackClass);
        } catch (RuntimeException e) {
            try {
                return (API) ConstructorUtils.invokeConstructor(fallbackClass);
            } catch (Exception ee) {
                if (log.isErrorEnabled()) {
                    log.error(ee.getMessage(), ee);
                }
                return null;
            }
        }
    }
}