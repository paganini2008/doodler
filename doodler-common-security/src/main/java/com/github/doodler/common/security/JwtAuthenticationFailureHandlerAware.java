package com.github.doodler.common.security;

import static com.github.doodler.common.Constants.REQUEST_HEADER_TIMESTAMP;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.github.doodler.common.ApiResult;
import com.github.doodler.common.ErrorCode;
import com.github.doodler.common.context.HttpRequestContextHolder;
import com.github.doodler.common.context.MessageLocalization;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: JwtAuthenticationFailureHandlerAware
 * @Author: Fred Feng
 * @Date: 28/11/2023
 * @Version 1.0.0
 */
@Slf4j
@Order(81)
@RestControllerAdvice
public class JwtAuthenticationFailureHandlerAware {

    @Autowired
    private JwtAuthenticationFailureListener jwtAuthenticationFailureListener;

    @Autowired
    private MessageLocalization messageLocalization;

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<ApiResult<?>> handleJwtAuthenticationException(HttpServletRequest request,
                                                                         JwtAuthenticationException e) {
        jwtAuthenticationFailureListener.onAuthenticationFailed(e);
        return getEntity(request, e);
    }

    private ResponseEntity<ApiResult<?>> getEntity(HttpServletRequest request, AuthenticationException e) {
        ErrorCode errorCode = ErrorCodes.matches(e);
        if (log.isErrorEnabled() && errorCode.isFatal()) {
            log.error(e.getMessage(), e);
        }
        ApiResult<Object> result = ApiResult.failed(getErrorMessage(errorCode), errorCode.getCode(), null);
        result.setRequestPath(request.getRequestURI());
        String timestamp = HttpRequestContextHolder.getHeader(REQUEST_HEADER_TIMESTAMP);
        if (StringUtils.isNotBlank(timestamp)) {
            result.setElapsed(System.currentTimeMillis() - Long.parseLong(timestamp));
        }
        return new ResponseEntity<ApiResult<?>>(result, HttpStatus.UNAUTHORIZED);
    }

    private String getErrorMessage(ErrorCode errorCode) {
        Locale locale = HttpRequestContextHolder.getLocale();
        return messageLocalization.getMessage(errorCode, locale);
    }
}