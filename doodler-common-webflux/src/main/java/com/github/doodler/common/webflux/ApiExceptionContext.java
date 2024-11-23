package com.github.doodler.common.webflux;

import java.util.List;

import org.springframework.stereotype.Component;

import com.github.doodler.common.ThrowableInfo;
import com.github.doodler.common.utils.LruList;

/**
 * 
 * @Description: ApiExceptionContext
 * @Author: Fred Feng
 * @Date: 17/11/2024
 * @Version 1.0.0
 */
@Component
public class ApiExceptionContext {

    private final List<ThrowableInfo> exceptionTraces = new LruList<>(256);

    public List<ThrowableInfo> getExceptionTraces() {
        return exceptionTraces;
    }

}
