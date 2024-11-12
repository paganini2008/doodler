package com.github.doodler.common.webmvc;

import static com.github.doodler.common.Constants.REQUEST_HEADER_PARENT_SPAN_ID;
import static com.github.doodler.common.Constants.REQUEST_HEADER_SPAN_ID;
import static com.github.doodler.common.Constants.REQUEST_HEADER_TIMESTAMP;
import static com.github.doodler.common.Constants.REQUEST_HEADER_TRACES;
import static com.github.doodler.common.Constants.REQUEST_HEADER_TRACE_ID;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import com.github.doodler.common.ApiResult;
import com.github.doodler.common.BizException;
import com.github.doodler.common.ErrorCode;
import com.github.doodler.common.ExceptionTransformer;
import com.github.doodler.common.context.HttpRequestContextHolder;
import com.github.doodler.common.context.MessageLocalization;
import com.github.doodler.common.context.Span;
import com.github.doodler.common.utils.LangUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: GlobalExceptionHandler
 * @Author: Fred Feng
 * @Date: 15/11/2022
 * @Version 1.0.0
 */
@Slf4j
@Order(200)
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private MessageLocalization messageLocalization;

    @Autowired
    private ExceptionTransformer exceptionTransformer;

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(BizException.class)
    public ResponseEntity<ApiResult<?>> handleBizException(HttpServletRequest request,
            HttpServletResponse response, BizException e) {
        ErrorCode errorCode = e.getErrorCode();
        if (log.isErrorEnabled() && errorCode.isFatal()) {
            log.error(e.getMessage(), e);
        }

        ApiResult<Object> result =
                ApiResult.failed(getErrorMessage(errorCode, LangUtils.toObjectArray(e.getArg())),
                        errorCode.getCode(), e.getArg());
        result.setRequestPath(request.getRequestURI());

        String timestamp = HttpRequestContextHolder.getHeader(REQUEST_HEADER_TIMESTAMP);
        if (StringUtils.isNotBlank(timestamp)) {
            result.setElapsed(System.currentTimeMillis() - Long.parseLong(timestamp));
        }
        traceApi(result, request, response, e.getHttpStatus().value());
        return new ResponseEntity<>(result, e.getHttpStatus());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResult<?>> handleNoHandlerFoundException(HttpServletRequest request,
            HttpServletResponse response, NoHandlerFoundException e) {
        if (log.isErrorEnabled()) {
            log.error(e.getMessage(), e);
        }
        ApiResult<Object> result = ApiResult.failed("NO_HANDLER_FOUND");
        result.setRequestPath(request.getRequestURI());
        String timestamp = HttpRequestContextHolder.getHeader(REQUEST_HEADER_TIMESTAMP);
        if (StringUtils.isNotBlank(timestamp)) {
            result.setElapsed(System.currentTimeMillis() - Long.parseLong(timestamp));
        }
        traceApi(result, request, response, 404);
        return new ResponseEntity<ApiResult<?>>(result, HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult<?>> handleException(HttpServletRequest request,
            HttpServletResponse response, Exception e) {
        if (log.isErrorEnabled()) {
            log.error(e.getMessage(), e);
        }
        Throwable te = exceptionTransformer.transform(e);
        ApiResult<Object> result;
        if (te instanceof BizException) {
            BizException bte = (BizException) te;
            result = ApiResult.failed(
                    getErrorMessage(bte.getErrorCode(), LangUtils.toObjectArray(bte.getArg())),
                    bte.getErrorCode().getCode(), bte.getArg());
        } else {
            result = ApiResult.failed("INTERNAL_SERVER_ERROR: " + te.getMessage());
        }
        result.setRequestPath(request.getRequestURI());
        String timestamp = HttpRequestContextHolder.getHeader(REQUEST_HEADER_TIMESTAMP);
        if (StringUtils.isNotBlank(timestamp)) {
            result.setElapsed(System.currentTimeMillis() - Long.parseLong(timestamp));
        }
        traceApi(result, request, response, 500);
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String getErrorMessage(ErrorCode errorCode, Object[] args) {
        Locale locale = HttpRequestContextHolder.getLocale();
        return messageLocalization.getMessage(errorCode, locale, args);
    }

    private void traceApi(Object result, HttpServletRequest request, HttpServletResponse response,
            int status) {
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
            span.setParentSpanId(
                    Integer.parseInt(httpHeaders.getFirst(REQUEST_HEADER_PARENT_SPAN_ID)));
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
