package com.github.doodler.common.utils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description: StringKeyMatchedMap
 * @Author: Fred Feng
 * @Date: 26/03/2023
 * @Version 1.0.0
 */
public class StringKeyMatchedMap<V> extends KeyMatchedMap<String, V> {

    private static final long serialVersionUID = -8578286064402795926L;

    public StringKeyMatchedMap(MatchMode matchMode) {
        this(matchMode, true);
    }

    public StringKeyMatchedMap(MatchMode matchMode, boolean matchFirst) {
        this(new LinkedHashMap<>(), matchMode, matchFirst);
    }

    public StringKeyMatchedMap(Map<String, V> delegate, MatchMode matchMode, boolean matchFirst) {
        super(delegate, matchFirst);
        this.matchMode = matchMode;
    }

    private final MatchMode matchMode;

    @Override
    protected boolean match(String key, Object inputKey) {
        return matchMode.matches(key, (String) inputKey);
    }
}