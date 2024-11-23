package com.github.doodler.common.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Function;

/**
 * 
 * @Description: TimeWindowUnit
 * @Author: Fred Feng
 * @Date: 17/11/2024
 * @Version 1.0.0
 */
public enum TimeWindowUnit {

    DAYS {

        @Override
        public int sizeOf(int span, int days) {
            return span * days;
        }

        @Override
        public LocalDateTime locate(Instant timestamp, TimeZone timeZone, int span) {
            LocalDateTime ldt = timestamp.atZone(timeZone.toZoneId()).toLocalDate().atTime(0, 0);
            return ldt;
        }

        @Override
        public Map<String, Object> initializeMap(Date endTime,
                                                 int span,
                                                 int size,
                                                 TimeZone timeZone,
                                                 DateTimeFormatter df,
                                                 Function<Long, Object> f) {
            Map<String, Object> data = new LinkedHashMap<>();
            LocalDateTime ldt = locate(endTime.toInstant(), timeZone, span);
            ldt = ldt.withHour(0).withMinute(0).withSecond(0);
            ZoneOffset zos = OffsetDateTime.now(timeZone != null ? timeZone.toZoneId() :
                    ZoneId.systemDefault()).getOffset();
            for (int i = 0; i < size; i++) {
                data.put(ldt.format(df), f.apply(ldt.toInstant(zos).toEpochMilli()));
                ldt = ldt.plusDays(-1 * span);
            }
            return MapUtils.reverse(data);
        }
    },

    HOURS {

        @Override
        public LocalDateTime locate(Instant timestamp, TimeZone timeZone, int span) {
            final ZonedDateTime zdt = timestamp.atZone(timeZone.toZoneId());
            int hour = zdt.getHour();
            return LocalDateTime.of(zdt.toLocalDate(), LocalTime.of(hour - hour % span, 0, 0));
        }

        @Override
        public int sizeOf(int span, int days) {
            return (24 % span == 0 ? (24 / span) : (24 / span + 1)) * days;
        }

        @Override
        public Map<String, Object> initializeMap(Date endTime,
                                                 int span,
                                                 int size,
                                                 TimeZone timeZone,
                                                 DateTimeFormatter df,
                                                 Function<Long, Object> f) {
            Map<String, Object> data = new LinkedHashMap<>();
            LocalDateTime ldt = locate(endTime.toInstant(), timeZone, span);
            ldt = ldt.withMinute(0).withSecond(0);
            ZoneOffset zos = OffsetDateTime.now(timeZone != null ? timeZone.toZoneId() :
                    ZoneId.systemDefault()).getOffset();
            for (int i = 0; i < size; i++) {
                data.put(ldt.format(df), f.apply(ldt.toInstant(zos).toEpochMilli()));
                ldt = ldt.plusHours(-1 * span);
            }
            return MapUtils.reverse(data);
        }

    },
    MINUTES {

        @Override
        public LocalDateTime locate(Instant timestamp, TimeZone timeZone, int span) {
            final ZonedDateTime zdt = timestamp.atZone(timeZone.toZoneId());
            int hour = zdt.getHour();
            int minute = zdt.getMinute();
            return LocalDateTime.of(zdt.toLocalDate(), LocalTime.of(hour, minute - minute % span, 0));
        }

        @Override
        public int sizeOf(int span, int days) {
            return (60 % span == 0 ? (60 / span) : (60 / span + 1)) * 24 * days;
        }

        @Override
        public Map<String, Object> initializeMap(Date endTime,
                                                 int span,
                                                 int size,
                                                 TimeZone timeZone,
                                                 DateTimeFormatter df,
                                                 Function<Long, Object> f) {
            Map<String, Object> data = new LinkedHashMap<>();
            LocalDateTime ldt = locate(endTime.toInstant(), timeZone, span);
            ldt = ldt.withSecond(0);
            ZoneOffset zos = OffsetDateTime.now(timeZone != null ? timeZone.toZoneId() :
                    ZoneId.systemDefault()).getOffset();
            for (int i = 0; i < size; i++) {
                data.put(ldt.format(df), f.apply(ldt.toInstant(zos).toEpochMilli()));
                ldt = ldt.plusMinutes(-1 * span);
            }
            return MapUtils.reverse(data);
        }

    },
    SECONDS {

        @Override
        public LocalDateTime locate(Instant timestamp, TimeZone timeZone, int span) {
            final ZonedDateTime zdt = timestamp.atZone(timeZone.toZoneId());
            int hour = zdt.getHour();
            int minute = zdt.getMinute();
            int second = zdt.getSecond();
            return LocalDateTime.of(zdt.toLocalDate(), LocalTime.of(hour, minute, second - second % span));
        }

        @Override
        public int sizeOf(int span, int days) {
            return (60 % span == 0 ? (60 / span) : (60 / span + 1)) * 60 * 24 * days;
        }

        @Override
        public Map<String, Object> initializeMap(Date endTime,
                                                 int span,
                                                 int size,
                                                 TimeZone timeZone,
                                                 DateTimeFormatter df,
                                                 Function<Long, Object> f) {
            Map<String, Object> data = new LinkedHashMap<>();
            LocalDateTime ldt = locate(endTime.toInstant(), timeZone, span);
            ZoneOffset zos = OffsetDateTime.now(timeZone != null ? timeZone.toZoneId() :
                    ZoneId.systemDefault()).getOffset();
            for (int i = 0; i < size; i++) {
                data.put(ldt.format(df), f.apply(ldt.toInstant(zos).toEpochMilli()));
                ldt = ldt.plusSeconds(-1 * span);
            }
            return MapUtils.reverse(data);
        }

    };

    public abstract LocalDateTime locate(Instant timestamp, TimeZone timeZone, int span);

    public abstract int sizeOf(int span, int days);

    public abstract Map<String, Object> initializeMap(Date endTime,
                                                      int span,
                                                      int size,
                                                      TimeZone timeZone,
                                                      DateTimeFormatter df,
                                                      Function<Long, Object> f);

}
