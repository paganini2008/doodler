package io.doodler.common.feign;

import static io.doodler.common.Constants.REQUEST_HEADER_PARENT_SPAN_ID;
import static io.doodler.common.Constants.REQUEST_HEADER_SPAN_ID;
import static io.doodler.common.Constants.REQUEST_HEADER_TIMESTAMP;
import static io.doodler.common.Constants.REQUEST_HEADER_TRACES;
import static io.doodler.common.Constants.REQUEST_HEADER_TRACE_ID;

import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.doodler.common.ApiResult;
import io.doodler.common.ErrorCode;
import io.doodler.common.context.HttpRequestContextHolder;
import io.doodler.common.context.MessageLocalization;
import io.doodler.common.context.Span;
import io.doodler.common.utils.LangUtils;

/**
 * @Description: RestClientExceptionHandler
 * @Author: Fred Feng
 * @Date: 11/10/2023
 * @Version 1.0.0
 */
@Slf4j
@Order(100)
@RestControllerAdvice
public class RestClientExceptionHandler {

    @Autowired
    private MessageLocalization messageLocalization;

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ApiResult<?>> handleRestClientException(HttpServletRequest request, HttpServletResponse response,
                                                                  RestClientException e) {
        ErrorCode errorCode = e.getErrorCode();
        if (log.isErrorEnabled() && errorCode.isFatal()) {
            log.error("[RestClientError] request: {}, response: {}", e.getRequest().toString(),
                    e.getResponse() != null ? e.getResponse().toString() : "<None>");
        }

        ApiResult<Object> result = ApiResult.failed(getErrorMessage(errorCode, LangUtils.toObjectArray(e.getArg())),
                errorCode.getCode(), e.getArg());
        result.setRequestPath(request.getRequestURI());
        String timestamp = HttpRequestContextHolder.getHeader(REQUEST_HEADER_TIMESTAMP);
        if (StringUtils.isNotBlank(timestamp)) {
            result.setElapsed(System.currentTimeMillis() - Long.parseLong(timestamp));
        }
        traceApi(result, request, response, e.getHttpStatus().value());
        return new ResponseEntity<>(result, e.getHttpStatus());
    }

    private String getErrorMessage(ErrorCode errorCode, Object[] args) {
        Locale locale = HttpRequestContextHolder.getLocale();
        return messageLocalization.getMessage(errorCode, locale, args);
    }

    private void traceApi(Object result, HttpServletRequest request, HttpServletResponse response, int status) {
        HttpHeaders httpHeaders = HttpRequestContextHolder.getResponseHeaders();
        List<String> traces = httpHeaders.get(REQUEST_HEADER_TRACES);
        Span span = getSpan(result, request, status);
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
    }

    protected Span getSpan(Object body, HttpServletRequest request, int status) {
        try {
            HttpHeaders httpHeaders = HttpRequestContextHolder.getHeaders();
            String traceId = httpHeaders.getFirst(REQUEST_HEADER_TRACE_ID);
            int spanId = Integer.parseInt(httpHeaders.getFirst(REQUEST_HEADER_SPAN_ID));
            Span span = new Span(traceId, spanId);
            span.setParentSpanId(Integer.parseInt(httpHeaders.getFirst(REQUEST_HEADER_PARENT_SPAN_ID)));
            span.setTimestamp(Long.parseLong(httpHeaders.getFirst(REQUEST_HEADER_TIMESTAMP)));
            span.setPath(((ApiResult<?>) body).getRequestPath());
            span.setElapsed(((ApiResult<?>) body).getElapsed());
            span.setStatus(status);
            return span;
        } catch (Exception ignored) {
            return null;
        }
    }
}