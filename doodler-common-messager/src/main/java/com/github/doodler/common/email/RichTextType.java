package com.github.doodler.common.email;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.doodler.common.enums.EnumConstant;
import com.github.doodler.common.enums.EnumUtils;

/**
 * @Description: RichTextType
 * @Author: Fred Feng
 * @Date: 19/12/2022
 * @Version 1.0.0
 */
public enum RichTextType implements EnumConstant {

	HTML(0, "HTML"),
    MARKDOWN(1, "Markdown"),
    FREEMARKER(2, "Freemarker"),
    THYMELEAF(3, "Thymeleaf");
    
    private final int value;
    private final String repr;

    private RichTextType(int value, String repr) {
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
    public static RichTextType valueOf(Integer type) {
        return EnumUtils.valueOf(RichTextType.class, type);
    }
}