package com.github.doodler.common.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.doodler.common.enums.EnumConstant;
import com.github.doodler.common.enums.EnumUtils;

/**
 * @Description: LogoutReason
 * @Author: Fred Feng
 * @Date: 15/02/2023
 * @Version 1.0.0
 */
public enum LogoutReason implements EnumConstant {

    NORMAL_LOGOUT(0, "Normal logout"),

    ABNORMAL_LOGOUT(1, "Abnormal logout"),

    SESSION_EXPIRED(2, "Session Expired");

    private LogoutReason(int code, String repr) {
        this.code = code;
        this.repr = repr;
    }

    private final int code;
    private final String repr;

    @JsonValue
    @Override
    public Integer getValue() {
        return code;
    }

    @Override
    public String getRepr() {
        return repr;
    }

    @JsonCreator
    public static LogoutReason getBy(String value) {
        return EnumUtils.valueOf(LogoutReason.class, value);
    }
}