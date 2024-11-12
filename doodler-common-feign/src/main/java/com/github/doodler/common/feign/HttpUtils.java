package com.github.doodler.common.feign;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import feign.FeignException;
import feign.Request;
import feign.Response;
import lombok.experimental.UtilityClass;

/**
 * @Description: HttpUtils
 * @Author: Fred Feng
 * @Date: 16/01/2023
 * @Version 1.0.0
 */
@UtilityClass
public class HttpUtils {

    public static final String PROTOCOL_VERSION = "HTTP/1.1";

    public HttpStatus getHttpStatus(FeignException e) {
        return getHttpStatus(e.status());
    }

    public HttpStatus getHttpStatus(int status) {
        HttpStatus httpStatus;
        try {
            httpStatus = HttpStatus.valueOf(status);
        } catch (RuntimeException ignored) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return httpStatus;
    }

    public HttpHeaders getHttpHeaders(Request request) {
        HttpHeaders httpHeaders = new HttpHeaders();
        request.headers().entrySet().forEach(e -> {
            httpHeaders.addAll(e.getKey(), new ArrayList<>(e.getValue()));
        });
        return httpHeaders;
    }

    public HttpHeaders getHttpHeaders(Response response) {
        HttpHeaders httpHeaders = new HttpHeaders();
        response.headers().entrySet().forEach(e -> {
            httpHeaders.addAll(e.getKey(), new ArrayList<>(e.getValue()));
        });
        return httpHeaders;
    }

    public String getFirstHeader(Request request, String headerName) {
        if (MapUtils.isEmpty(request.headers())) {
            return "";
        }
        Collection<String> c = request.headers().get(headerName);
        if (CollectionUtils.isNotEmpty(c)) {
            return IterableUtils.first(c);
        }
        return null;
    }

    public String getRequestLine(Request request) {
        String method = request.httpMethod().name();
        String url = request.url();
        int index;
        if ((index = url.indexOf("?")) != -1) {
            url = url.substring(0, index);
        }
        return PROTOCOL_VERSION + " " + method + " " + url;
    }

    public HttpHeaders transferFrom(Map<String, ?> headerMap) {
        HttpHeaders httpHeaders = new HttpHeaders();
        headerMap.entrySet().forEach(e -> {
            if (e.getValue() != null) {
                httpHeaders.add(e.getKey(), e.getValue().toString());
            }
        });
        return httpHeaders;
    }
}