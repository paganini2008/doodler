package io.doodler.common.context;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.GenericFilterBean;

import io.doodler.common.Constants;

/**
 * @Description: ApiRealmFilter
 * @Author: Fred Feng
 * @Date: 13/02/2023
 * @Version 1.0.0
 */
public abstract class ApiRealmFilter extends GenericFilterBean {

    @Value("${spring.profiles.active}")
    private String env;

    @SneakyThrows
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        if (!(servletRequest instanceof HttpServletRequest)) {
            filterChain.doFilter(servletRequest, servletResponse);
        }
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (("local".equals(env) || isApiRealm(request)) && !shouldFilter(request)) {
            doInFilter(request, response, filterChain);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    protected abstract void doInFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException;

    protected boolean shouldFilter(HttpServletRequest request) {
        return false;
    }

    private boolean isApiRealm(HttpServletRequest request) {
        return Boolean.parseBoolean(request.getHeader(Constants.REQUEST_HEADER_API_REALM));
    }
}