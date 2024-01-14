package com.github.doodler.common.validation;

import static com.github.doodler.common.Constants.REQUEST_HEADER_TIMESTAMP;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.doodler.common.ApiResult;
import com.github.doodler.common.ErrorCode;
import com.github.doodler.common.context.HttpRequestContextHolder;
import com.github.doodler.common.context.MessageLocalization;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: ValidationExceptionHanlder
 * @Author: Fred Feng
 * @Date: 21/03/2020
 * @Version 1.0.0
 */
@Slf4j
@Order(100)
@RestControllerAdvice
public class ValidationExceptionHandler {

    private static final int DEFAULT_CODE = 9001400;

    @Autowired
    private MessageLocalization messageLocalization;

    @Autowired
    private ErrorCodeFinder errorCodeFinder;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResult<?>> handleValidationException(HttpServletRequest request,
                                                                  MethodArgumentNotValidException e)
            throws JsonProcessingException {
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        ObjectError firstError = errors.get(0);
        String field = ((FieldError) firstError).getField();
        Object rejectedValue = ((FieldError) firstError).getRejectedValue();
        String messageKey = firstError.getDefaultMessage();
        int code = DEFAULT_CODE;
        String defaultMessage = "";
        if (errorCodeFinder.containsKey(messageKey)) {
            ErrorCode errorCode = errorCodeFinder.lookup(messageKey);
            code = errorCode.getCode();
            defaultMessage = errorCode.getDefaultMessage();
        }
        Locale locale = HttpRequestContextHolder.getLocale();
        String msg = getI18nMessage(locale, messageKey, defaultMessage, rejectedValue);
        if (log.isErrorEnabled()) {
            log.error("[Validation Error] field: {}, rejectedValue: {}, msg: {}", field, rejectedValue, msg);
        }
        return new ResponseEntity<>(this.getApiResult(request, msg, code), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResult<?>> handleValidationException(HttpServletRequest request,
                                                                  ConstraintViolationException e) {
        List<ConstraintViolation<?>> errors = new ArrayList<ConstraintViolation<?>>(e.getConstraintViolations());
        ConstraintViolation<?> firstError = errors.get(0);
        String path = firstError.getPropertyPath().toString();
        String messageKey = firstError.getMessage();
        Object rejectedValue = firstError.getInvalidValue();
        int code = DEFAULT_CODE;
        String defaultMessage = "";
        if (errorCodeFinder.containsKey(messageKey)) {
            ErrorCode errorCode = errorCodeFinder.lookup(messageKey);
            code = errorCode.getCode();
            defaultMessage = errorCode.getDefaultMessage();
        }
        Locale locale = HttpRequestContextHolder.getLocale();
        String msg = getI18nMessage(locale, messageKey, defaultMessage, rejectedValue);
        if (log.isErrorEnabled()) {
            log.error("[Validation Error] path: {}, rejectedValue: {}, msg: {}", path, rejectedValue, msg);
        }
        return new ResponseEntity<>(this.getApiResult(request, msg, code), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResult<?>> handleBindingException(HttpServletRequest request, BindException e) {
        String path = request.getPathInfo();
        StringBuilder builder = new StringBuilder();
        for (FieldError error : e.getFieldErrors()) {
            builder.append(error.getDefaultMessage()).append(", ");
        }
        if (log.isErrorEnabled()) {
            log.error("[Validation Error] path: {}, msg: {}", path, builder);
        }
        return new ResponseEntity<>(this.getApiResult(request, builder.toString(), DEFAULT_CODE), HttpStatus.BAD_REQUEST);
    }

    private String getI18nMessage(Locale locale, String repr, String defaultMessage, Object rejectedValue) {
        String[] args = repr.split(":", 2);
        String msgKey, defMsg;
        if (args.length == 1) {
            msgKey = args[0];
            defMsg = StringUtils.isNotBlank(defaultMessage) ? defaultMessage : args[0];
        } else {
            msgKey = args[0];
            defMsg = StringUtils.isNotBlank(args[1]) ? args[1] :
                    StringUtils.isNotBlank(defaultMessage) ? defaultMessage : args[0];
        }
        return messageLocalization.getMessage(msgKey, locale, defMsg, rejectedValue);
    }

    private ApiResult<?> getApiResult(HttpServletRequest request, String msg, int code) {
        ApiResult<?> result = ApiResult.failed(msg, code);
        result.setRequestPath(request.getRequestURI());
        String timestamp = HttpRequestContextHolder.getHeader(REQUEST_HEADER_TIMESTAMP);
        if (StringUtils.isNotBlank(timestamp)) {
            result.setElapsed(System.currentTimeMillis() - Long.parseLong(timestamp));
        }
        return result;
    }
}