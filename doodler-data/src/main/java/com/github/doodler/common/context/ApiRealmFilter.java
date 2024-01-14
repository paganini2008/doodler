package com.github.doodler.common.context;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.GenericFilterBean;

import com.github.doodler.common.Constants;

/**
 * @Description: ApiRealmFilter
 * @Author: Fred Feng
 * @Date: 20/05/2020
 * @Version 1.0.0
 */
public abstract class ApiRealmFilter extends GenericFilterBean implements InitializingBean {

    @Value("${spring.profiles.active}")
    private String env;

    @Autowired(required = false)
    private ServerProperties serverProperties;

    private final PathMatcher pathMatcher = new AntPathMatcher();

    private final List<String> exposeServlets = new CopyOnWriteArrayList<>();

    protected ApiRealmFilter() {
    	exposeServlets("/druid/**");
    }

    @Override
    public void afterPropertiesSet() throws ServletException {
        if (serverProperties != null && serverProperties.getExposeServlets() != null) {
        	exposeServlets.addAll(serverProperties.getExposeServlets());
        }
    }

    public void exposeServlets(String... paths) {
        if (ArrayUtils.isNotEmpty(paths)) {
        	exposeServlets.addAll(Arrays.asList(paths));
        }
    }

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
        if (exposeServlets.isEmpty()) {
            return false;
        }
        String requestUri = request.getRequestURI();
        for (String servletPath : exposeServlets) {
            if (pathMatcher.match(servletPath, requestUri)) {
                return true;
            }
        }
        return false;
    }

    private boolean isApiRealm(HttpServletRequest request) {
        return Boolean.parseBoolean(request.getHeader(Constants.REQUEST_HEADER_API_REALM));
    }
}