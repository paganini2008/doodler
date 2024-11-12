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
public class DefaultRequestContextExchanger implements RequestContextExchanger {

    @Override
    public Object get() {
        return RequestContextHolder.currentRequestAttributes();
    }

    @Override
    public void set(Object obj) {
        if (obj != null) {
            RequestContextHolder.setRequestAttributes((RequestAttributes) obj, true);
        }
    }

    @Override
    public void reset() {
        RequestContextHolder.resetRequestAttributes();
    }
}