package com.github.doodler.common.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: TimeWindowMap
 * @Author: Fred Feng
 * @Date: 06/03/2023
 * @Version 1.0.0
 */
public class TimeWindowMap<V> extends MutableMap<Instant, V> {

    private static final long serialVersionUID = 6278189730292055007L;

    public TimeWindowMap(int span, TimeWindowUnit timeWindowUnit, TimeZone timeZone, int maxSize) {
        this(span, timeWindowUnit, timeZone, maxSize, null);
    }

    public TimeWindowMap(int span, TimeWindowUnit timeWindowUnit, TimeZone timeZone, int maxSize,
                         RemovalListener<V> removalListener) {
        super(new LruMap<>(new ConcurrentHashMap<>(), maxSize, removalListener));
        this.span = span;
        this.timeWindowUnit = timeWindowUnit;
        this.timeZone = Optional.ofNullable(timeZone).orElse(TimeZone.getDefault());
    }

    private final int span;
    private final TimeWindowUnit timeWindowUnit;
    private final TimeZone timeZone;

    @Override
    protected Instant mutate(Object inputKey) {
        LocalDateTime ldt = timeWindowUnit.locate((Instant) inputKey, timeZone, span);
        return ldt.atZone(timeZone.toZoneId()).toInstant();
    }
}