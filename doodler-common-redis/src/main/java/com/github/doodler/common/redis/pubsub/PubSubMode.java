package com.github.doodler.common.redis.pubsub;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.doodler.common.enums.EnumConstant;
import com.github.doodler.common.enums.EnumUtils;

/**
 * @Description: PubSubMode
 * @Author: Fred Feng
 * @Date: 14/12/2022
 * @Version 1.0.0
 */
public enum PubSubMode implements EnumConstant {
    /**
     * unicast
     */
    UNICAST(0, "unicast"),
    /**
     * multicast
     */
    MULTICAST(1, "multicast");

    private final Integer value;
    private final String repr;

    PubSubMode(Integer value, String repr) {
        this.value = value;
        this.repr = repr;
    }

    @Override
    @JsonValue
    public Integer getValue() {
        return value;
    }

    @Override
    public String getRepr() {
        return repr;
    }

    @JsonCreator
    public static PubSubMode valueOf(Integer type) {
        return EnumUtils.valueOf(PubSubMode.class, type);
    }
}