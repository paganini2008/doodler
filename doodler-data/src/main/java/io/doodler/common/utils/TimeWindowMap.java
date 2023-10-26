package io.doodler.common.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @Description: TimeWindowMap
 * @Author: Fred Feng
 * @Date: 06/03/2023
 * @Version 1.0.0
 */
public class TimeWindowMap<V> extends MutableMap<Instant, V> {

    private static final long serialVersionUID = 6278189730292055007L;

    public TimeWindowMap(int span, TimeWindowUnit timeWindowUnit) {
        super(new LruMap<>(60));
        this.span = span;
        this.timeWindowUnit = timeWindowUnit;
    }

    private final int span;
    private final TimeWindowUnit timeWindowUnit;

    @Override
    protected Instant mutate(Object inputKey) {
        LocalDateTime ldt = timeWindowUnit.locate((Instant) inputKey, span);
        return ldt.atZone(ZoneId.systemDefault()).toInstant();
    }
}