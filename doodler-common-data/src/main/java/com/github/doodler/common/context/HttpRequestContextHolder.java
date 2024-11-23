package com.github.doodler.common.context;

import static com.github.doodler.common.Constants.REQUEST_HEADER_REQUEST_ID;
import static com.github.doodler.common.Constants.REQUEST_HEADER_TIMESTAMP;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.github.doodler.common.utils.WebUtils;

import lombok.SneakyThrows;

/**
 * @Description: HttpRequestContextHolder
 * @Author: Fred Feng
 * @Date: 15/11/2022
 * @Version 1.0.0
 */
@Component
@ConditionalOnWebApplication(type = Type.SERVLET)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HttpRequestContextHolder extends ApiRealmFilter {

    private static final ThreadLocal<HttpRequestInfo> ttl = TransmittableThreadLocal.withInitial(
            () -> new HttpRequestInfo());

    public static String getHeader(String headerName) {
        return getHeader(headerName, "");
    }

    public static String getHeader(String headerName, String defaultValue) {
        HttpHeaders httpHeaders = getHeaders();
        if (httpHeaders == null) {
            return defaultValue;
        }
        String headerValue = httpHeaders.getFirst(headerName);
        if (StringUtils.isBlank(headerValue)) {
            headerValue = defaultValue;
        }
        return headerValue;
    }

    public static List<String> getHeaders(String headerName) {
        HttpHeaders httpHeaders = getHeaders();
        return httpHeaders.getOrDefault(headerName, Collections.emptyList());
    }

    public static void addHeader(String headerName, String headerValue) {
        HttpHeaders httpHeaders = getHeaders();
        httpHeaders.add(headerName, headerValue);
    }

    public static void addHeaders(String headerName, List<String> headerValues) {
        HttpHeaders httpHeaders = getHeaders();
        httpHeaders.addAll(headerName, headerValues);
    }

    public static void addHeaders(MultiValueMap<String, String> headerMap) {
        HttpHeaders httpHeaders = getHeaders();
        httpHeaders.addAll(headerMap);
    }

    public static void setHeaderIfAbsent(String headerName, String headerValue) {
        setHeaderIfAbsent(headerName, () -> headerValue);
    }

    public static void setHeaderIfAbsent(String headerName, Supplier<String> headerValue) {
        HttpHeaders httpHeaders = getHeaders();
        if (!httpHeaders.containsKey(headerName)) {
            httpHeaders.set(headerName, headerValue.get());
        }
    }

    public static void setHeader(String headerName, String headerValue) {
        HttpHeaders httpHeaders = getHeaders();
        httpHeaders.set(headerName, headerValue);
    }

    public static void setHeaders(Map<String, String> headerMap) {
        HttpHeaders httpHeaders = getHeaders();
        httpHeaders.setAll(headerMap);
    }

    public static HttpRequestInfo get() {
        return ttl.get();
    }

    public static HttpHeaders getHeaders() {
        return get().getRequestHeaders();
    }

    public static HttpHeaders getResponseHeaders() {
        return get().getResponseHeaders();
    }

    public static Locale getLocale() {
        HttpHeaders headers = getHeaders();
        List<Locale> locales = headers.getAcceptLanguageAsLocales();
        if (CollectionUtils.isNotEmpty(locales)) {
            return locales.get(0);
        }
        String lang = headers.getFirst("lang");
        if (StringUtils.isNotBlank(lang)) {
            return LocaleUtils.toLocale(lang);
        }
        return LocaleContextHolder.getLocale();
    }

    public static String getPath() {
        return get().getPath();
    }

    public static void set(HttpRequestInfo httpRequestInfo) {
        ttl.set(httpRequestInfo);
    }

    public static void clear() {
        ttl.remove();
    }

    @Override
    @SneakyThrows
    protected void doInFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        String path = request.getRequestURI();
        String queryString = request.getQueryString();
        if (StringUtils.isNotBlank(queryString)) {
            path += "?" + queryString;
        }
        HttpHeaders httpHeaders = WebUtils.copyHeaders(request);
        HttpRequestInfo httpRequestInfo = new HttpRequestInfo(request.getMethod(), path, httpHeaders);
        applySettings(httpRequestInfo, request, response);
        ttl.set(httpRequestInfo);

        filterChain.doFilter(request, response);
        clear();
    }

    protected void applySettings(HttpRequestInfo httpRequestInfo, HttpServletRequest request,
                                 HttpServletResponse response) {
        HttpHeaders httpHeaders = httpRequestInfo.getRequestHeaders();
        if (!httpHeaders.containsKey(REQUEST_HEADER_REQUEST_ID)) {
            httpHeaders.set(REQUEST_HEADER_REQUEST_ID, UUID.randomUUID().toString());
        }
        if (!httpHeaders.containsKey(REQUEST_HEADER_TIMESTAMP)) {
            httpHeaders.set(REQUEST_HEADER_TIMESTAMP, String.valueOf(System.currentTimeMillis()));
        }
        MediaType mediaType = httpHeaders.getContentType();
        if ((mediaType != null) && (mediaType.compareTo(MediaType.MULTIPART_FORM_DATA) != 0) && (mediaType.compareTo(
                MediaType.APPLICATION_FORM_URLENCODED) == 0)) {
            httpRequestInfo.setRequestBody(WebUtils.toParameterString(request));
        }
    }
}