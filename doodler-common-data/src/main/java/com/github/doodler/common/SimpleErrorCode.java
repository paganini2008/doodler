package com.github.doodler.common;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: SimpleErrorCode
 * @Author: Fred Feng
 * @Date: 15/11/2022
 * @Version 1.0.0
 */
@Getter
@Setter
@ToString
public class SimpleErrorCode implements ErrorCode {

    private String messageKey;
    private int code;
    private String defaultMessage;
    private boolean fatal;

    public SimpleErrorCode(String messageKey, int code) {
        this(messageKey, code, "");
    }

    public SimpleErrorCode(String messageKey, int code, String defaultMessage) {
        this(messageKey, code, defaultMessage, true);
    }

    public SimpleErrorCode(String messageKey, int code, String defaultMessage, boolean fatal) {
        this.messageKey = messageKey;
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.fatal = fatal;
    }
}