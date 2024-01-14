package com.github.doodler.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 
 * @Description: EventType
 * @Author: Fred Feng
 * @Date: 14/03/2022
 * @Version 1.0.0
 */
public enum EventType implements EnumConstant {

    TEST("test");

    private final String value;

    EventType(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String getRepr() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    @JsonCreator
    public static EventType getBy(String value) {
        return EnumUtils.valueOf(EventType.class, value);
    }
}