package com.github.doodler.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 
 * @Description: ServiceInfo
 * @Author: Fred Feng
 * @Date: 14/03/2022
 * @Version 1.0.0
 */
public enum ServiceInfo implements EnumConstant {

    ACCOUNT("doodler-account-service", "account", "/acc");

    private final String fullName;
    private final String shortName;
    private final String contextPath;

    private ServiceInfo(String fullName, String shortName, String contextPath) {
        this.fullName = fullName;
        this.shortName = shortName;
        this.contextPath = contextPath;
    }

    @Override
    @JsonValue
    public String getValue() {
        return fullName;
    }

    @Override
    public String getRepr() {
        return this.fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public String getContextPath() {
        return contextPath;
    }

    @Override
    public String toString() {
        return this.fullName;
    }

    @JsonCreator
    public static ServiceInfo get(String value) {
        return EnumUtils.valueOf(ServiceInfo.class, value);
    }
}