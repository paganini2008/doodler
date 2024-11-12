package com.github.doodler.common.utils;

import com.google.common.base.CaseFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: KeyCaseFormatMap
 * @Author: Fred Feng
 * @Date: 24/03/2023
 * @Version 1.0.0
 */
public class KeyCaseFormatMap extends KeyConvertibleMap<String, Object> {

    private static final long serialVersionUID = 8451823505397115565L;

    public KeyCaseFormatMap(CaseFormat fromCaseFormat, CaseFormat toCaseFormat) {
        this(new HashMap<>(), fromCaseFormat, toCaseFormat);
    }

    public KeyCaseFormatMap(Map<String, Object> delegate, CaseFormat fromCaseFormat, CaseFormat toCaseFormat) {
        super(delegate);
        this.fromCaseFormat = fromCaseFormat;
        this.toCaseFormat = toCaseFormat;
    }

    private final CaseFormat fromCaseFormat;
    private final CaseFormat toCaseFormat;

    @Override
    protected Object convertKey(Object key) {
        return fromCaseFormat.converterTo(toCaseFormat).convert(key.toString());
    }
}