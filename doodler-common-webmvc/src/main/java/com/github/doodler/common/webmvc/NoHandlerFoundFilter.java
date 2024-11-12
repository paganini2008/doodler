package com.github.doodler.common.webmvc;

import java.io.IOException;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.NoHandlerFoundException;
import com.github.doodler.common.context.ApiRealmFilter;
import com.github.doodler.common.context.ContextPath;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * @Description: Fix the bug that spring security framework preprocess no-mapping handler rather
 *               than processing by security filter chain
 * @Author: Fred Feng
 * @Date: 08/12/2022
 * @Version 1.0.0
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 5)
@Profile({"dev", "test", "prod"})
@ConditionalOnClass(name = {"org.springframework.security.web.SecurityFilterChain"})
@Component
@RequiredArgsConstructor
public class NoHandlerFoundFilter extends ApiRealmFilter {

    private final DispatcherServlet dispatcherServlet;
    private final ContextPath contextPath;

    @Override
    protected void doInFilter(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws IOException, ServletException {
        String pathPrefix = contextPath.getContextPath();
        if (StringUtils.isNotBlank(pathPrefix)) {
            String requestUrl = request.getRequestURI();
            if (requestUrl.startsWith(pathPrefix)) {
                if (getHandler(request) == null) {
                    NoHandlerFoundException noHandlerFoundException = new NoHandlerFoundException(
                            request.getMethod(), getRequestPath(request),
                            new ServletServerHttpRequest(request).getHeaders());
                    throw noHandlerFoundException;
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    @SneakyThrows
    protected HandlerExecutionChain getHandler(HttpServletRequest request) {
        Map<String, HandlerMapping> requestMappings = BeanFactoryUtils
                .beansOfTypeIncludingAncestors(dispatcherServlet.getWebApplicationContext(),
                        HandlerMapping.class, true, false);
        for (HandlerMapping handlerMapping : requestMappings.values()) {
            try {
                HandlerExecutionChain handlerExecutionChain = handlerMapping.getHandler(request);
                if (handlerExecutionChain != null
                        && handlerExecutionChain.getHandler() instanceof HandlerMethod) {
                    return handlerExecutionChain;
                }
            } catch (RuntimeException ignored) {
                break;
            }
        }
        return null;
    }

    protected String getRequestPath(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
