package com.github.doodler.common.webflux;

import java.time.LocalDateTime;

import org.apache.commons.lang3.LocaleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;

import com.github.doodler.common.ApiResult;
import com.github.doodler.common.ErrorCode;
import com.github.doodler.common.ExceptionDescriptor;
import com.github.doodler.common.ExceptionTransformer;
import com.github.doodler.common.ThrowableInfo;
import com.github.doodler.common.context.MessageLocalization;
import com.github.doodler.common.utils.ExceptionUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * GlobalExceptionHandler
 *
 * @author Fred Feng
 * @version 1.0.0
 */
@Slf4j
@Order(200)
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private ApiExceptionContext context;

    @Autowired
    private MessageLocalization messageLocalization;

    @Autowired
    private ExceptionTransformer exceptionTransformer;

    @ExceptionHandler(Throwable.class)
    public ApiResult<String> handleException(ServerWebExchange exchange, Throwable e) {
        if (log.isErrorEnabled()) {
            log.error(e.getMessage(), e);
        }
        String path = exchange.getRequest().getPath().pathWithinApplication().value();
        ThrowableInfo throwableInfo = new ThrowableInfo(path, e.getMessage(), ExceptionUtils.toArray(e),
                LocalDateTime.now());
        context.getExceptionTraces().add(throwableInfo);

        e = exceptionTransformer.transform(e);
        ApiResult<String> result;
        if (e instanceof ExceptionDescriptor) {
            ExceptionDescriptor descriptor = (ExceptionDescriptor) e;
            ErrorCode errorCode = descriptor.getErrorCode();
            String lang = exchange.getRequest().getHeaders().getFirst("lang");
            result = ApiResult.failed(messageLocalization.getMessage(errorCode, LocaleUtils.toLocale(lang)));
        } else {
            result = ApiResult.failed(e.getMessage());
        }
        result.setRequestPath(path);
        return result;
    }
}
