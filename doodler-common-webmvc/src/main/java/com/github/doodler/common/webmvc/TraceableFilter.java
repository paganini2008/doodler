package com.github.doodler.common.webmvc;

import static com.github.doodler.common.Constants.REQUEST_HEADER_PARENT_SPAN_ID;
import static com.github.doodler.common.Constants.REQUEST_HEADER_SPAN_ID;
import static com.github.doodler.common.Constants.REQUEST_HEADER_TRACE_ID;
import java.io.IOException;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import com.github.doodler.common.context.ApiRealmFilter;
import com.github.doodler.common.context.HttpRequestContextHolder;

/**
 * @Description: TraceableFilter
 * @Author: Fred Feng
 * @Date: 25/02/2023
 * @Version 1.0.0
 */
public class TraceableFilter extends ApiRealmFilter implements Ordered {

    @Override
    protected void doInFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpHeaders httpHeaders = HttpRequestContextHolder.getHeaders();
        if (!httpHeaders.containsKey(REQUEST_HEADER_TRACE_ID)) {
            httpHeaders.set(REQUEST_HEADER_TRACE_ID, UUID.randomUUID().toString());
        }
        int parentSpanId, spanId;
        if (httpHeaders.containsKey(REQUEST_HEADER_PARENT_SPAN_ID)) {
            parentSpanId = Integer.parseInt(httpHeaders.getFirst(REQUEST_HEADER_PARENT_SPAN_ID));
        } else {
            parentSpanId = 0;
            httpHeaders.set(REQUEST_HEADER_PARENT_SPAN_ID, String.valueOf(parentSpanId));
        }
        if (httpHeaders.containsKey(REQUEST_HEADER_SPAN_ID)) {
            spanId = Integer.parseInt(httpHeaders.getFirst(REQUEST_HEADER_SPAN_ID));
        } else {
            spanId = parentSpanId + 1;
            httpHeaders.set(REQUEST_HEADER_SPAN_ID, String.valueOf(spanId));
        }
        chain.doFilter(request, response);
    }

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE + 1;
	}
    
}