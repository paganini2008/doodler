package com.github.doodler.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @Description: GameTag
 * @Author: Fred Feng
 * @Date: 07/11/2023
 * @Version 1.0.0
 */
public enum GameTag implements EnumConstant {

    TOP("TOP"),

    HOT("HOT"),

    NEW("NEW");

    private final String value;

    GameTag(String value) {
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
    public static GameTag getBy(String value) {
        return EnumUtils.valueOf(GameTag.class, value.toUpperCase());
    }
}