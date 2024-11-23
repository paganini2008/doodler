package com.github.doodler.common.webflux;

import static com.github.doodler.common.Constants.REQUEST_HEADER_REQUEST_ID;
import static com.github.doodler.common.Constants.REQUEST_HEADER_TIMESTAMP;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.github.doodler.common.context.HttpRequestInfo;

import reactor.core.publisher.Mono;
import reactor.util.context.Context;

/**
 * 
 * @Description: ReactiveHttpRequestContextHolder
 * @Author: Fred Feng
 * @Date: 18/11/2024
 * @Version 1.0.0
 */
@Component
public class ReactiveHttpRequestContextHolder implements WebFilter {

    private static final String KEY_HTTP_REQUEST_CONTEXT = HttpRequestInfo.class.getName() + ".CONTEXT-KEY";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().pathWithinApplication().value();
        HttpRequestInfo httpRequestInfo = new HttpRequestInfo(
                exchange.getRequest().getMethodValue(),
                path,
                new HttpHeaders(exchange.getRequest().getHeaders()));
        HttpHeaders httpHeaders = httpRequestInfo.getRequestHeaders();
        String requestId = exchange.getRequest().getHeaders().getFirst(REQUEST_HEADER_REQUEST_ID);
        if (StringUtils.isBlank(requestId)) {
            requestId = UUID.randomUUID().toString();
            httpHeaders.set(REQUEST_HEADER_REQUEST_ID, requestId);
        }
        httpRequestInfo.setRequestId(requestId);

        String timestamp = exchange.getRequest().getHeaders().getFirst(REQUEST_HEADER_TIMESTAMP);
        if (StringUtils.isBlank(timestamp)) {
            timestamp = String.valueOf(System.currentTimeMillis());
            httpHeaders.set(REQUEST_HEADER_TIMESTAMP, timestamp);
        }
        httpRequestInfo.setTimestmap(System.currentTimeMillis());
        MediaType mediaType = httpRequestInfo.getRequestHeaders().getContentType();
        if ((mediaType != null) && (mediaType.compareTo(MediaType.MULTIPART_FORM_DATA) != 0) && (mediaType.compareTo(
                MediaType.APPLICATION_FORM_URLENCODED) == 0)) {
            //httpRequestInfo.setRequestBody(WebUtils.toParameterString(request));
        }
        return chain.filter(exchange).contextWrite(Context.of(KEY_HTTP_REQUEST_CONTEXT, httpRequestInfo));
    }

}
