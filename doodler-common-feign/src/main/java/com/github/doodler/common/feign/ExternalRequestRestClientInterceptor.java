package com.github.doodler.common.feign;

import static com.github.doodler.common.Constants.REQUEST_HEADER_PARENT_SPAN_ID;
import static com.github.doodler.common.Constants.REQUEST_HEADER_SPAN_ID;
import static com.github.doodler.common.Constants.REQUEST_HEADER_TIMESTAMP;
import static com.github.doodler.common.Constants.REQUEST_HEADER_TRACES;
import static com.github.doodler.common.Constants.REQUEST_HEADER_TRACE_ID;
import org.springframework.http.HttpHeaders;
import com.github.doodler.common.context.HttpRequestContextHolder;
import com.github.doodler.common.context.Span;
import com.github.doodler.common.utils.ThreadLocalInteger;
import com.github.doodler.common.utils.WebUtils;
import feign.Request;
import feign.RequestTemplate;
import feign.Response;

/**
 * @Description: ExternalRequestRestClientInterceptor
 * @Author: Fred Feng
 * @Date: 25/02/2023
 * @Version 1.0.0
 */
public class ExternalRequestRestClientInterceptor implements RestClientInterceptor {

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
        HttpHeaders httpHeaders = HttpRequestContextHolder.getResponseHeaders();
        Span span = getSpan(request, response);
        httpHeaders.add(REQUEST_HEADER_TRACES, span.toString());
    }

    protected Span getSpan(Request request, Response response) {
        String traceId = HttpUtils.getFirstHeader(request, REQUEST_HEADER_TRACE_ID);
        int spanId = Integer.parseInt(HttpUtils.getFirstHeader(request, REQUEST_HEADER_SPAN_ID));
        Span span = new Span(traceId, spanId);
        span.setParentSpanId(Integer.parseInt(HttpUtils.getFirstHeader(request, REQUEST_HEADER_PARENT_SPAN_ID)));
        span.setTimestamp(Long.parseLong(HttpUtils.getFirstHeader(request, REQUEST_HEADER_TIMESTAMP)));
        span.setPath(request.url());
        span.setElapsed(System.currentTimeMillis() - span.getTimestamp());
        span.setStatus(response.status());
        span.setThirdparty(true);
        return span;
    }

    @Override
    public void afterCompletion(Request request, Response response, Exception e) {
        postHandle(request, response);
    }
}