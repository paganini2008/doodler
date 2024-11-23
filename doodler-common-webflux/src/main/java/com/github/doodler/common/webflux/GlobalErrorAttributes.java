package com.github.doodler.common.webflux;

import java.util.Map;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.web.reactive.function.server.ServerRequest;

import com.github.doodler.common.ExceptionDescriptor;

/**
 * 
 * @Description: GlobalErrorAttributes
 * @Author: Fred Feng
 * @Date: 17/11/2024
 * @Version 1.0.0
 */
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(request, options);
        Throwable e = this.getError(request);
        if (e instanceof ExceptionDescriptor) {
            errorAttributes.put("errorCode", ((ExceptionDescriptor) e).getErrorCode());
        }
        return errorAttributes;
    }

}
