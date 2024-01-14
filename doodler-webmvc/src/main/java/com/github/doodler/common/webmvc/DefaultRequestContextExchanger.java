package com.github.doodler.common.webmvc;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.github.doodler.common.context.RequestContextExchanger;

/**
 * @Description: DefaultRequestContextExchanger
 * @Author: Fred Feng
 * @Date: 11/12/2023
 * @Version 1.0.0
 */
@Component
public class DefaultRequestContextExchanger implements RequestContextExchanger<RequestAttributes> {

    @Override
    public RequestAttributes get() {
        return RequestContextHolder.currentRequestAttributes();
    }

    @Override
    public void set(RequestAttributes obj) {
        if (obj != null) {
            RequestContextHolder.setRequestAttributes(obj, true);
        }
    }

    @Override
    public void reset() {
        RequestContextHolder.resetRequestAttributes();
    }
}