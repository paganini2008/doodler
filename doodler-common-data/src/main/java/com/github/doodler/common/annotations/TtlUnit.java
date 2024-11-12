package com.github.doodler.common.annotations;

import java.time.Duration;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description: TtlUnit
 * @Author: Fred Feng
 * @Date: 30/01/2023
 * @Version 1.0.0
 */
public enum TtlUnit {

    SECONDS(TimeUnit.SECONDS) {

        @Override
        public Duration getDuration(long value) {
            return Duration.ofSeconds(value);
        }
    },

    MINUTES(TimeUnit.MINUTES) {

        @Override
        public Duration getDuration(long value) {
            return Duration.ofMinutes(value);
        }
    },

    HOURS(TimeUnit.HOURS) {

        @Override
        public Duration getDuration(long value) {
            return Duration.ofHours(value);
        }
    },

    DAYS(TimeUnit.DAYS) {

        @Override
        public Duration getDuration(long value) {
            return Duration.ofDays(value);
        }
    };

    private final TimeUnit timeUnit;

    private TtlUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public abstract Duration getDuration(long value);

    public static TtlUnit getBy(TimeUnit timeUnit) {
        return cache.get(timeUnit);
    }

    private static final Map<TimeUnit, TtlUnit> cache = new EnumMap<TimeUnit, TtlUnit>(TimeUnit.class);

    static {

        for (TtlUnit ttlType : TtlUnit.values()) {
            cache.put(ttlType.getTimeUnit(), ttlType);
        }
    }
}