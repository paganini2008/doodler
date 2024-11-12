package com.github.doodler.common.feign;

import feign.Feign;
import java.util.Map;

/**
 * @Description: RestClientCustomizer
 * @Author: Fred Feng
 * @Date: 28/11/2022
 * @Version 1.0.0
 */
public interface RestClientCustomizer {

    void customize(Feign.Builder builder, String serviceId, String beanName, Class<?> interfaceClass,
                   Map<String, Object> metaData);
}