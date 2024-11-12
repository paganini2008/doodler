package com.github.doodler.common.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import com.github.doodler.common.ErrorCode;
import com.github.doodler.common.ExceptionDescriptor;
import com.github.doodler.common.utils.LangUtils;

/**
 * @Description: UserIdentifierNotFoundException
 * @Author: Fred Feng
 * @Date: 11/05/2023
 * @Version 1.0.0
 */
public class UserIdentifierNotFoundException extends InternalAuthenticationServiceException implements ExceptionDescriptor {

	private static final long serialVersionUID = 4292324063198219810L;

	public UserIdentifierNotFoundException(ErrorCode errorCode, Object arg) {
        super(getDefaultMessage(errorCode, arg));
        this.errorCode = errorCode;
        this.arg = arg;
    }

    public UserIdentifierNotFoundException(ErrorCode errorCode, Object arg, Throwable e) {
        super(getDefaultMessage(errorCode, arg), e);
        this.errorCode = errorCode;
        this.arg = arg;
    }

    private final ErrorCode errorCode;

    private final Object arg;

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    @Override
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    @Override
    public Object getArg() {
        return arg;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }
    
    private static String getDefaultMessage(ErrorCode errorCode, Object arg) {
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