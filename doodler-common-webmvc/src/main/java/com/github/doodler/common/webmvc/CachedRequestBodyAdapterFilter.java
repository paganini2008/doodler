package com.github.doodler.common.webmvc;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.github.doodler.common.context.ApiRealmFilter;
import com.github.doodler.common.context.HttpRequestContextHolder;
import com.github.doodler.common.context.HttpRequestInfo;

import lombok.SneakyThrows;

/**
 * @Description: CachedRequestBodyAdapterFilter
 * @Author: Fred Feng
 * @Date: 18/01/2023
 * @Version 1.0.0
 */
@SuppressWarnings("all")
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class CachedRequestBodyAdapterFilter extends ApiRealmFilter {

    @Override
    @SneakyThrows
    protected void doInFilter(HttpServletRequest httpRequest, HttpServletResponse httpResponse, FilterChain filterChain) {
        ServletRequest requestWrapper = null;
        if (hasRequestBody(httpRequest)) {
            requestWrapper = new CachedRequestBodyServletRequest(httpRequest);
        }
        if (requestWrapper == null) {
            filterChain.doFilter(httpRequest, httpResponse);
        } else {
            String payload = getPayload(requestWrapper.getInputStream());
            if (StringUtils.isNotBlank(payload)) {
                HttpRequestInfo httpRequestInfo = HttpRequestContextHolder.get();
                httpRequestInfo.setRequestBody(payload);
            }
            filterChain.doFilter(requestWrapper, httpResponse);
        }
    }

    private String getPayload(InputStream input) {
        StringBuilder str = new StringBuilder();
        LineIterator it = IOUtils.lineIterator(input, StandardCharsets.UTF_8);
        while (it.hasNext()) {
            str.append(it.next().trim());
        }
        return str.toString();
    }

    private boolean hasRequestBody(HttpServletRequest request) {
        if (StringUtils.isBlank(request.getContentType())) {
            return false;
        }
        HttpMethod httpMethod = HttpMethod.valueOf(request.getMethod());
        if (httpMethod != HttpMethod.POST && httpMethod != HttpMethod.PUT) {
            return false;
        }
        MediaType mediaType = MediaType.valueOf(request.getContentType());
        return (mediaType.compareTo(MediaType.APPLICATION_JSON) == 0) ||
                (mediaType.compareTo(MediaType.APPLICATION_JSON_UTF8) == 0);
    }
}