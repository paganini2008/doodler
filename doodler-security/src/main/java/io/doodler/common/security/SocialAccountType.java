package io.doodler.common.security;

import io.doodler.common.enums.EnumConstant;
import io.doodler.common.enums.EnumUtils;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @Description: SocialAccountType
 * @Author: Fred Feng
 * @Date: 21/02/2023
 * @Version 1.0.0
 */
public enum SocialAccountType implements EnumConstant {

    TWITCH("twitch", "Twitch"),

    FACEBOOK("facebook", "Facebook"),

    GOOGLE("google", "Google"),

    LINE("line", "Line");

    private final String value;
    private final String repr;

    private SocialAccountType(String value, String repr) {
        this.value = value;
        this.repr = repr;
    }

    @Override
    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String getRepr() {
        return this.repr;
    }

    @JsonCreator
    public static SocialAccountType getBy(String value) {
        return EnumUtils.valueOf(SocialAccountType.class, value);
    }
}