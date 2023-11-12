package io.doodler.common.webmvc;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.util.WebUtils;

import io.doodler.common.context.ApiRealmFilter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * @Description: Fix the bug that spring security framework preprocess no-mapping handler
 * rather than processing by security filter chain
 * @Author: Fred Feng
 * @Date: 08/12/2022
 * @Version 1.0.0
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 5)
@Component
@RequiredArgsConstructor
public class NoHandlerFoundFilter extends ApiRealmFilter {

    private final DispatcherServlet dispatcherServlet;

    @Value("${spring.mvc.servlet.path}")
    private String servletContextPath;

    @Override
    protected void doInFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        if (getHandler(request) == null) {
            String requestUrl = request.getRequestURI();
            if ("/".equals(requestUrl)) {
                response.sendRedirect(servletContextPath + "/");
                return;
            }
            throw new NoHandlerFoundException(request.getMethod(), getRequestUri(request),
                    new ServletServerHttpRequest(request).getHeaders());
        }
        filterChain.doFilter(request, response);
    }

    @SneakyThrows
    protected HandlerExecutionChain getHandler(HttpServletRequest request) {
        if (dispatcherServlet.getHandlerMappings() != null) {
            for (HandlerMapping mapping : dispatcherServlet.getHandlerMappings()) {
                HandlerExecutionChain handler = mapping.getHandler(request);
                if (handler != null) {
                    return handler;
                }
            }
        }
        return null;
    }

    private static String getRequestUri(HttpServletRequest request) {
        String uri = (String) request.getAttribute(WebUtils.INCLUDE_REQUEST_URI_ATTRIBUTE);
        if (StringUtils.isBlank(uri)) {
            uri = request.getRequestURI();
        }
        return uri;
    }
}