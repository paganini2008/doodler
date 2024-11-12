package com.github.doodler.common.enums;

/**
 * @Description: EnumConstant
 * @Author: Fred Feng
 * @Date: 15/11/2022
 * @Version 1.0.0
 */
public interface EnumConstant {

    String DEFAULT_GROUP = "DEFAULT";

    Object getValue();

    String getRepr();

    default String getGroup() {
        return DEFAULT_GROUP;
    }
}