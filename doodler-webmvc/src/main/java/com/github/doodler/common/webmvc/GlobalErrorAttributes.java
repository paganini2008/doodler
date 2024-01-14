package com.github.doodler.common.webmvc;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import com.github.doodler.common.ExceptionDescriptor;
import com.github.doodler.common.ExceptionTransferer;

/**
 * @Description: GlobalErrorAttributes
 * @Author: Fred Feng
 * @Date: 21/03/2020
 * @Version 1.0.0
 */
@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    @Autowired(required = false)
    private ExceptionTransferer exceptionTransferer;

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);
        Throwable e = super.getError(webRequest);
        if (exceptionTransferer != null) {
            e = exceptionTransferer.transfer(e);
        }
        errorAttributes.put("errorObject", e);
        if (e instanceof ExceptionDescriptor) {
            errorAttributes.put("errorCode", ((ExceptionDescriptor) e).getErrorCode());
            errorAttributes.put("errorArg", ((ExceptionDescriptor) e).getArg());
        }
        return errorAttributes;
    }
}