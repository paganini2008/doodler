package com.github.doodler.common.webmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description: WebResponsePreHandler
 * @Author: Fred Feng
 * @Date: 25/02/2023
 * @Version 1.0.0
 */
public interface WebResponsePreHandler {

    default boolean supports(Class<?> resultClass, HttpServletRequest request, HttpServletResponse response) {
        return (request != null && response != null);
    }

    Object beforeBodyWrite(Object body, HttpServletRequest request, HttpServletResponse response);
}