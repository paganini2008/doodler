package com.github.doodler.common.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import com.github.doodler.common.Constants;
import com.github.doodler.common.utils.ExceptionUtils;
import com.github.doodler.common.utils.WebUtils;

/**
 * @Description: WebRequestCompletionAdvice
 * @Author: Fred Feng
 * @Date: 13/02/2023
 * @Version 1.0.0
 */
public abstract class WebRequestCompletionAdvice implements WebMvcInterceptor {

    @Value("${spring.profiles.active}")
    private String env;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception e) throws Exception {
        if (!shouldFilter(request) && ("local".equals(env) || isApiRealm(request))) {
            HttpRequestInfo httpRequestInfo = HttpRequestContextHolder.get();
            if (httpRequestInfo.getResponseHeaders() == null) {
                httpRequestInfo.addResponseHeaders(WebUtils.copyHeaders(response));
            }
            if (httpRequestInfo.getStatus() == null) {
                httpRequestInfo.setStatus(HttpStatus.valueOf(response.getStatus()));
            }
            if (ArrayUtils.isEmpty(httpRequestInfo.getErrors())) {
                httpRequestInfo.setErrors(ExceptionUtils.toArray(e));
            }
            doAfterCompletion(request, response, handler, e);
        }
    }

    protected abstract void doAfterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                              Exception e) throws Exception;

    protected boolean shouldFilter(HttpServletRequest request) {
        return false;
    }

    protected boolean isApiRealm(HttpServletRequest request) {
        return Boolean.parseBoolean(request.getHeader(Constants.REQUEST_HEADER_API_REALM));
    }
}