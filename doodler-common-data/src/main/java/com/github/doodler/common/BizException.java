package com.github.doodler.common;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import com.github.doodler.common.utils.LangUtils;

/**
 * @Description: BizException
 * @Author: Fred Feng
 * @Date: 15/11/2022
 * @Version 1.0.0
 */
public class BizException extends RuntimeException implements ExceptionDescriptor {

    private static final long serialVersionUID = 1L;
    private final ErrorCode errorCode;
    private final HttpStatus httpStatus;
    private final Object arg;

    public BizException(ErrorCode errorCode) {
        this(errorCode, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public BizException(ErrorCode errorCode, Object arg) {
        this(errorCode, HttpStatus.INTERNAL_SERVER_ERROR, arg);
    }

    public BizException(ErrorCode errorCode, Throwable e) {
        this(errorCode, HttpStatus.INTERNAL_SERVER_ERROR, e);
    }

    public BizException(ErrorCode errorCode, HttpStatus httpStatus) {
        this(errorCode, httpStatus, (Object) null);
    }

    public BizException(ErrorCode errorCode, HttpStatus httpStatus, Object arg) {
        super(getDisplayMessage(errorCode, arg));
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.arg = arg;
    }

    public BizException(ErrorCode errorCode, HttpStatus httpStatus, Throwable e) {
        this(errorCode, httpStatus, e, null);
    }

    public BizException(ErrorCode errorCode, HttpStatus httpStatus, Throwable e, Object arg) {
        super(getDisplayMessage(errorCode, arg), e);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.arg = arg;
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    @Override
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public Object getArg() {
        return arg;
    }

    private static String getDisplayMessage(ErrorCode errorCode, Object arg) {
        String defaultMessage = errorCode.getDefaultMessage();
        if (StringUtils.isNotBlank(defaultMessage)) {
            if (arg != null) {
                defaultMessage = String.format(defaultMessage, LangUtils.toObjectArray(arg));
            }
            return String.format(DEFAULT_MESSAGE_FORMAT, errorCode.getCode(), errorCode.getMessageKey(), defaultMessage);
        }
        return String.format(DEFAULT_MESSAGE_FORMAT, errorCode.getCode(), errorCode.getMessageKey(), "<None>");
    }
}