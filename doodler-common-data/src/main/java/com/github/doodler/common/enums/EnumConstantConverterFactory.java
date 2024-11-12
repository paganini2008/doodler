package com.github.doodler.common.enums;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

/**
 * @Description: EnumConstantConverterFactory
 * @Author: Fred Feng
 * @Date: 26/01/2023
 * @Version 1.0.0
 */
public class EnumConstantConverterFactory implements ConverterFactory<String, EnumConstant> {

    @Override
    public <T extends EnumConstant> Converter<String, T> getConverter(Class<T> targetType) {
        return source -> {
            if (source == null) {
                return null;
            }
            try {
                return EnumUtils.valueOf(targetType, source);
            } catch (RuntimeException ignored) {
                return null;
            }
        };
    }
}