package com.github.doodler.common.feign;

import static com.github.doodler.common.Constants.REQUEST_HEADER_PARENT_SPAN_ID;
import static com.github.doodler.common.Constants.REQUEST_HEADER_SPAN_ID;
import static com.github.doodler.common.Constants.REQUEST_HEADER_TIMESTAMP;
import static com.github.doodler.common.Constants.REQUEST_HEADER_TRACES;
import static com.github.doodler.common.Constants.REQUEST_HEADER_TRACE_ID;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.HttpHeaders;
import com.github.doodler.common.context.HttpRequestContextHolder;
import com.github.doodler.common.utils.ThreadLocalInteger;
import com.github.doodler.common.utils.WebUtils;
import feign.Request;
import feign.RequestTemplate;
import feign.Response;

/**
 * @Description: TraceableRestClientInterceptor
 * @Author: Fred Feng
 * @Date: 25/02/2023
 * @Version 1.0.0
 */
public class TraceableRestClientInterceptor implements RestClientInterceptor {

    private static final int MAX_RESPONSE_HEADER_SIZE = 255;

    private final ThreadLocalInteger counter = new ThreadLocalInteger();

    @Override
    public void preHandle(Request request) {
        if (!WebUtils.hasCurrentRequest()) {
            return;
        }

        RequestTemplate template = request.requestTemplate();
        HttpHeaders httpHeaders = HttpRequestContextHolder.getHeaders();
        template.header(REQUEST_HEADER_TRACE_ID, httpHeaders.getFirst(REQUEST_HEADER_TRACE_ID));
        int currentSpanId;
        try {
            currentSpanId = Integer.parseInt(httpHeaders.getFirst(REQUEST_HEADER_SPAN_ID));
        } catch (RuntimeException ignored) {
            currentSpanId = 0;
        }
        template.header(REQUEST_HEADER_PARENT_SPAN_ID, String.valueOf(currentSpanId));
        int nextSpanId = currentSpanId + counter.incrementAndGet();
        template.header(REQUEST_HEADER_SPAN_ID, String.valueOf(nextSpanId));
        template.header(REQUEST_HEADER_TIMESTAMP, String.valueOf(System.currentTimeMillis()));
    }

    @Override
    public void postHandle(Request request, Response response) {
        if (!WebUtils.hasCurrentRequest()) {
            return;
        }
        HttpHeaders responseHeaders = HttpUtils.getHttpHeaders(response);
        List<String> traces = responseHeaders.get(REQUEST_HEADER_TRACES);
        HttpHeaders httpHeaders = HttpRequestContextHolder.getResponseHeaders();
        if (httpHeaders.size() <= MAX_RESPONSE_HEADER_SIZE && CollectionUtils.isNotEmpty(traces)) {
            httpHeaders.addAll(REQUEST_HEADER_TRACES, traces);
        }
        counter.reset();
    }

    @Override
    public void afterCompletion(Request request, Response response, Exception e) {
        postHandle(request, response);
    }
}