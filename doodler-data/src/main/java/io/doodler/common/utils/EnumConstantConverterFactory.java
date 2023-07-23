package io.doodler.common.utils;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import io.doodler.common.enums.EnumConstant;
import io.doodler.common.enums.EnumUtils;

/**
 * @Description: EnumConstantConverterFactory
 * @Author: Fred Feng
 * @Date: 26/01/2023
 * @Version 1.0.0
 */
public class EnumConstantConverterFactory implements ConverterFactory<Object, EnumConstant> {

    @Override
    public <T extends EnumConstant> Converter<Object, T> getConverter(Class<T> targetType) {
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