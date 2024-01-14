package com.github.doodler.common.amqp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.doodler.common.enums.EnumConstant;
import com.github.doodler.common.enums.EnumUtils;

/**
 * @Description: ApplicationQueue
 * @Author: Fred Feng
 * @Date: 13/04/2023
 * @Version 1.0.0
 */
public enum ApplicationQueue implements EnumConstant {

    USER("doodler-user-service", "doodler.queue.user"),

    UPMS("doodler-upms-service", "doodler.queue.upms"),

    COMMON("doodler-common-service", "doodler.queue.common");

    private final String value;
    private final String repr;

    private ApplicationQueue(String value, String repr) {
        this.value = value;
        this.repr = repr;
    }

    @JsonValue
    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getRepr() {
        return repr;
    }

    @JsonCreator
    public static ApplicationQueue forName(String value) {
        return EnumUtils.valueOf(ApplicationQueue.class, value);
    }
}