package com.github.doodler.common.webmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import com.github.doodler.common.ApiResult;
import com.github.doodler.common.context.HttpRequestContextHolder;
import com.github.doodler.common.context.HttpRequestInfo;
import com.github.doodler.common.utils.WebUtils;

/**
 * @Description: HttpRequestInfoWebResponsePreHandler
 * @Author: Fred Feng
 * @Date: 25/02/2023
 * @Version 1.0.0
 */
@Order(1)
@Component
public class HttpRequestInfoWebResponsePreHandler implements WebResponsePreHandler {

    @Override
    public boolean supports(Class<?> resultClass, HttpServletRequest request, HttpServletResponse response) {
        return resultClass.equals(ApiResult.class) && (request != null && response != null);
    }

    @Override
    public Object beforeBodyWrite(Object body, HttpServletRequest request, HttpServletResponse response) {
        HttpRequestInfo httpRequestInfo = HttpRequestContextHolder.get();
        httpRequestInfo.setResponseBody(body);
        if (httpRequestInfo.getResponseHeaders() == null) {
            httpRequestInfo.addResponseHeaders(WebUtils.copyHeaders(response));
        }
        if (httpRequestInfo.getStatus() == null) {
            httpRequestInfo.setStatus(HttpStatus.valueOf(response.getStatus()));
        }
        return body;
    }
}