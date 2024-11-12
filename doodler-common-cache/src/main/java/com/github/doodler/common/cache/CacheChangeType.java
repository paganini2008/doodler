package com.github.doodler.common.cache;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.doodler.common.enums.EnumConstant;
import com.github.doodler.common.enums.EnumUtils;

/**
 * @Description: CacheEventType
 * @Author: Fred Feng
 * @Date: 31/01/2023
 * @Version 1.0.0
 */
public enum CacheChangeType implements EnumConstant {

    PUT(0, "put"), EVICT(1, "evict"), CLEAR(2, "clear");

    private final int value;
    private final String repr;

    private CacheChangeType(int value, String repr) {
        this.value = value;
        this.repr = repr;
    }

    @JsonValue
    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public String getRepr() {
        return repr;
    }

    @JsonCreator
    public static CacheChangeType valueOf(Integer type) {
        return EnumUtils.valueOf(CacheChangeType.class, type);
    }
}