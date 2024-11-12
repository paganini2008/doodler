package com.github.doodler.common.webmvc;

import org.springframework.stereotype.Component;
import com.github.doodler.common.context.HttpRequestContextHolder;
import com.github.doodler.common.context.HttpRequestInfo;
import com.github.doodler.common.context.RequestContextExchanger;

/**
 * @Description: HttpRequestInfoExchanger
 * @Author: Fred Feng
 * @Date: 21/09/2023
 * @Version 1.0.0
 */
@Component
public class HttpRequestInfoExchanger implements RequestContextExchanger {

    @Override
    public Object get() {
        return HttpRequestContextHolder.get();
    }

    @Override
    public void set(Object obj) {
        HttpRequestContextHolder.set((HttpRequestInfo) obj);
    }

    @Override
    public void reset() {
        HttpRequestContextHolder.clear();
    }
}