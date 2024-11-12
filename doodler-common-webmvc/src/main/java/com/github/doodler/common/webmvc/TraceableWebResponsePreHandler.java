package com.github.doodler.common.webmvc;

import static com.github.doodler.common.Constants.REQUEST_HEADER_PARENT_SPAN_ID;
import static com.github.doodler.common.Constants.REQUEST_HEADER_SPAN_ID;
import static com.github.doodler.common.Constants.REQUEST_HEADER_TIMESTAMP;
import static com.github.doodler.common.Constants.REQUEST_HEADER_TRACES;
import static com.github.doodler.common.Constants.REQUEST_HEADER_TRACE_ID;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import com.github.doodler.common.ApiResult;
import com.github.doodler.common.context.HttpRequestContextHolder;
import com.github.doodler.common.context.Span;

/**
 * @Description: TraceableWebResponsePreHandler
 * @Author: Fred Feng
 * @Date: 25/02/2023
 * @Version 1.0.0
 */
@Slf4j
public class TraceableWebResponsePreHandler implements WebResponsePreHandler, Ordered {

    private static final List<String> specialUrls = Collections.unmodifiableList(
            Arrays.asList("/", "/ping", "/favicon.ico", "/error"));

    @Override
    public boolean supports(Class<?> resultClass, HttpServletRequest request, HttpServletResponse response) {
        return resultClass.equals(ApiResult.class) && (request != null && response != null) &&
                shouldApply(request, response);
    }

    protected boolean shouldApply(HttpServletRequest request, HttpServletResponse response) {
        return specialUrls.stream().noneMatch(url -> request.getRequestURI().endsWith(url));
    }

    @Override
    public Object beforeBodyWrite(Object body, HttpServletRequest request, HttpServletResponse response) {
        HttpHeaders httpHeaders = HttpRequestContextHolder.getResponseHeaders();
        List<String> traces = httpHeaders.get(REQUEST_HEADER_TRACES);
        Span span = getSpan(body, request, response);
        if (span != null) {
            if (CollectionUtils.isNotEmpty(traces)) {
                traces.add(span.toString());
            } else {
                httpHeaders.add(REQUEST_HEADER_TRACES, span.toString());
            }
        }
        traces = httpHeaders.get(REQUEST_HEADER_TRACES);
        if (CollectionUtils.isNotEmpty(traces)) {
            try {
                traces.forEach(trace -> {
                    response.addHeader(REQUEST_HEADER_TRACES, trace);
                });
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return body;
    }

    protected Span getSpan(Object body, HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpHeaders httpHeaders = HttpRequestContextHolder.getHeaders();
            String traceId = httpHeaders.getFirst(REQUEST_HEADER_TRACE_ID);
            int spanId = Integer.parseInt(httpHeaders.getFirst(REQUEST_HEADER_SPAN_ID));
            Span span = new Span(traceId, spanId);
            span.setParentSpanId(Integer.parseInt(httpHeaders.getFirst(REQUEST_HEADER_PARENT_SPAN_ID)));
            span.setTimestamp(Long.parseLong(httpHeaders.getFirst(REQUEST_HEADER_TIMESTAMP)));
            span.setPath(((ApiResult<?>) body).getRequestPath());
            span.setElapsed(((ApiResult<?>) body).getElapsed());
            span.setStatus(response.getStatus());
            return span;
        } catch (Exception ingored) {
            return null;
        }
    }

    @Override
    public int getOrder() {
        return 100;
    }
}