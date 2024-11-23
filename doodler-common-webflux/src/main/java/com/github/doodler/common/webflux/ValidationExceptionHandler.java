package com.github.doodler.common.webflux;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import com.github.doodler.common.ApiResult;
import com.github.doodler.common.context.MessageLocalization;

import reactor.core.publisher.Mono;

/**
 * 
 * ValidationExceptionHandler
 *
 * @author Fred Feng
 * @version 1.0.0
 */
@Order(100)
@RestControllerAdvice
public class ValidationExceptionHandler {

    @Autowired
    private MessageLocalization messageLocalization;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ApiResult<?>> handleException(WebExchangeBindException exception) {
        List<FieldError> fieldErrors = exception.getFieldErrors();
        if (CollectionUtils.isEmpty(fieldErrors)) {
            return Mono.just(ApiResult.failed());
        }
        return ReactiveRequestContextHolder.getCurrentRequest().flatMap(r -> {
            String lang = r.getHeaders().getFirst("lang");
            String message = fieldErrors.get(0).getDefaultMessage();
            return Mono.just(ApiResult.failed(getI18nMessage(lang, message)));
        });
    }

    private String getI18nMessage(String lang, String repr) {
        String[] args = repr.split(":", 2);
        String messageKey, defaultMessage;
        if (args.length == 1) {
            messageKey = args[0];
            defaultMessage = args[0];
        } else {
            messageKey = args[0];
            defaultMessage = args[1];
        }
        String message = messageLocalization.getMessage(messageKey, LocaleUtils.toLocale(lang), null, defaultMessage);
        if (StringUtils.isBlank(message)) {
            message = defaultMessage;
        }
        return message;
    }

}
