package com.github.doodler.common.utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @Description: CaseInsensitiveMap
 * @Author: Fred Feng
 * @Date: 24/03/2023
 * @Version 1.0.0
 */
public class CaseInsensitiveMap<T> extends KeyConvertibleMap<String, T> {

    private static final long serialVersionUID = 7726273319207033245L;

    public CaseInsensitiveMap() {
        this(new HashMap<>());
    }

    public CaseInsensitiveMap(Map<String, T> delegate) {
        super(delegate);
    }

    @Override
    protected Object convertKey(Object key) {
        if (key != null) {
            return key.toString().toLowerCase(Locale.ENGLISH);
        }
        return "";
    }
}