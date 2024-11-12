package com.github.doodler.common.webmvc;

import static com.github.doodler.common.Constants.REQUEST_HEADER_PARENT_SPAN_ID;
import static com.github.doodler.common.Constants.REQUEST_HEADER_SPAN_ID;
import static com.github.doodler.common.Constants.REQUEST_HEADER_TIMESTAMP;
import static com.github.doodler.common.Constants.REQUEST_HEADER_TRACES;
import static com.github.doodler.common.Constants.REQUEST_HEADER_TRACE_ID;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.error.ErrorAttributeOptions.Include;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.NoHandlerFoundException;
import com.github.doodler.common.ApiResult;
import com.github.doodler.common.ErrorCode;
import com.github.doodler.common.context.HttpRequestContextHolder;
import com.github.doodler.common.context.MessageLocalization;
import com.github.doodler.common.context.Span;
import com.github.doodler.common.utils.LangUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: GlobalErrorController
 * @Author: Fred Feng
 * @Date: 15/11/2022
 * @Version 1.0.0
 */
@Slf4j
@RestController
public class GlobalErrorController extends AbstractErrorController {

    private static final String ERROR_PATH = "/error";

    @Autowired
    public GlobalErrorController(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    @Autowired
    private MessageLocalization messageLocalization;

    @RequestMapping(value = ERROR_PATH, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ApiResult<Object>> error(HttpServletRequest request,
            HttpServletResponse response) {
        final Map<String, Object> errorAttributes =
                getErrorAttributes(request, ErrorAttributeOptions.of(Include.STACK_TRACE));
        ErrorCode errorCode = (ErrorCode) errorAttributes.get("errorCode");
        if (errorCode == null || errorCode.isFatal()) {
            if (log.isErrorEnabled()) {
                log.error("ErrorAttributes: " + errorAttributes.toString());
            }
        }
        HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());
        String message = "INTERNAL_SERVER_ERROR";
        int code = 0;
        if (errorCode != null) {
            Object errorArg = errorAttributes.get("errorArg");
            message = getErrorMessage(errorCode, LangUtils.toObjectArray(errorArg));
            code = errorCode.getCode();
        } else {
            Throwable e = (Throwable) errorAttributes.get("errorObject");
            if (e instanceof NoHandlerFoundException) {
                httpStatus = HttpStatus.NOT_FOUND;
                message = httpStatus.getReasonPhrase();
            } else {
                message = (String) errorAttributes.get("message");
                if (StringUtils.isBlank(message)) {
                    message = (String) errorAttributes.get("error");
                }
                if (StringUtils.isBlank(message)) {
                    if (e != null) {
                        message = e.getMessage();
                    }
                }
                if (StringUtils.isBlank(message)) {
                    message = httpStatus.getReasonPhrase();
                }
            }
        }
        ApiResult<Object> result = ApiResult.failed(message, code, null);
        result.setRequestPath((String) errorAttributes.get("path"));
        String timestamp = HttpRequestContextHolder.getHeader(REQUEST_HEADER_TIMESTAMP);
        if (StringUtils.isNotBlank(timestamp)) {
            result.setElapsed(System.currentTimeMillis() - Long.parseLong(timestamp));
        }
        traceApi(result, request, response);

        return new ResponseEntity<ApiResult<Object>>(result, httpStatus);
    }

    private String getErrorMessage(ErrorCode errorCode, Object[] args) {
        Locale locale = HttpRequestContextHolder.getLocale();
        return messageLocalization.getMessage(errorCode, locale, args);
    }

    private void traceApi(Object result, HttpServletRequest request, HttpServletResponse response) {
        HttpHeaders httpHeaders = HttpRequestContextHolder.getResponseHeaders();
        List<String> traces = httpHeaders.get(REQUEST_HEADER_TRACES);
        Span span = getSpan(result, request, response);
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

    protected Span getSpan(Object body, HttpServletRequest request, HttpServletResponse response) {
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
            span.setStatus(response.getStatus());
            return span;
        } catch (Exception ignored) {
            return null;
        }
    }

    // @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }
}
