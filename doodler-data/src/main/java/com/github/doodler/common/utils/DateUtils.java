package com.github.doodler.common.utils;

import java.text.ParseException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import com.github.doodler.common.Constants;

import lombok.experimental.UtilityClass;

/**
 * @Description: DateUtils
 * @Author: Fred Feng
 * @Date: 22/01/2020
 * @Version 1.0.0
 */
@UtilityClass
public class DateUtils {

    public Date toDate(LocalDate date) {
        return toDate(date, null);
    }

    public Date toDate(LocalDate date, Date defaultValue) {
        if (date != null) {
            return Date.from(date.atTime(LocalTime.of(0, 0, 0)).atZone(ZoneId.systemDefault()).toInstant());
        }
        return defaultValue;
    }

    public Date toDate(LocalDateTime date) {
        return toDate(date, null);
    }

    public Date toDate(LocalDateTime date, Date defaultValue) {
        if (date != null) {
            return Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
        }
        return defaultValue;
    }
    
    public Date toUTCDate(LocalDateTime date) {
    	return toUTCDate(date, null);
    }
    
    public Date toUTCDate(LocalDateTime date, Date defaultValue) {
        if (date != null) {
            return Date.from(date.atOffset(ZoneOffset.UTC).toInstant());
        }
        return defaultValue;
    }

    public Date toDate(String str) {
        try {
            return org.apache.commons.lang3.time.DateUtils.parseDate(str, Locale.ENGLISH,
                    Constants.SUPPORTED_DATE_TIME_PATTERNS);
        } catch (ParseException e) {
            throw new DateTimeException(e.getMessage(), e);
        }
    }

    public LocalDate toLocalDate(String str) {
        try {
            return LocalDate.parse(str, DateTimeFormatter.ISO_DATE);
        } catch (RuntimeException e) {
            Date date = toDate(str);
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
    }

    public LocalDateTime toLocalDateTime(String str) {
        try {
            return LocalDateTime.parse(str, DateTimeFormatter.ISO_DATE_TIME);
        } catch (RuntimeException e) {
            Date date = toDate(str);
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
    }

    public LocalDate toLocalDate(String str, String datePattern) {
        return LocalDate.parse(str, DateTimeFormatter.ofPattern(datePattern));
    }

    public LocalDateTime toLocalDateTime(String str, String datePattern) {
        return LocalDateTime.parse(str, DateTimeFormatter.ofPattern(datePattern));
    }

    public LocalDateTime toLocalDateTime(Date date) {
        return toLocalDateTime(date, null);
    }

    public LocalDateTime toLocalDateTime(Date date, LocalDateTime defaultValue) {
        if (date != null) {
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
        return defaultValue;
    }

    public Iterator<Date> dateIterator(Date from, int amount, int calendarField, Date to) {
        return new DateIterator(from, amount, calendarField, to);
    }

    public Iterator<LocalDate> localDateIterator(LocalDate startDate, int amount, ChronoUnit chronoUnit,
                                                 LocalDate endDate) {
        return new LocalDateIterator(startDate, amount, chronoUnit, endDate);
    }

    public Iterator<LocalDateTime> localDateTimeIterator(LocalDateTime startTime, int amount, ChronoUnit chronoUnit,
                                                         LocalDateTime endTime) {
        return new LocalDateTimeIterator(startTime, amount, chronoUnit, endTime);
    }

    static class DateIterator implements Iterator<Date> {

        private final Date to;
        private final int amount;
        private final int calendarField;
        private final Calendar calendar;

        DateIterator(Date from, int amount, int calendarField, Date to) {
            Calendar c = Calendar.getInstance();
            c.setTime(from);
            this.calendar = c;
            this.amount = amount;
            this.calendarField = calendarField;
            this.to = to;
        }

        @Override
        public boolean hasNext() {
            return calendar.getTime().compareTo(to) < 0;
        }

        @Override
        public Date next() {
            Calendar copy = (Calendar) calendar.clone();
            calendar.add(calendarField, amount);
            return copy.getTime();
        }
    }

    static class LocalDateIterator implements Iterator<LocalDate> {

        private LocalDate startDate;
        private final int amount;
        private final ChronoUnit chronoUnit;
        private final LocalDate endDate;

        public LocalDateIterator(LocalDate startDate, int amount, ChronoUnit chronoUnit, LocalDate endDate) {
            this.startDate = startDate;
            this.amount = amount;
            this.chronoUnit = chronoUnit;
            this.endDate = endDate;
        }

        @Override
        public boolean hasNext() {
            return startDate.compareTo(endDate) < 0;
        }

        @Override
        public LocalDate next() {
            LocalDate copy = LocalDate.of(startDate.getYear(), startDate.getMonth(), startDate.getDayOfMonth());
            startDate = startDate.plus(amount, chronoUnit);
            return copy;
        }
    }

    static class LocalDateTimeIterator implements Iterator<LocalDateTime> {

        private LocalDateTime startTime;
        private final int amount;
        private final ChronoUnit chronoUnit;
        private final LocalDateTime endTime;

        LocalDateTimeIterator(LocalDateTime startTime, int amount, ChronoUnit chronoUnit, LocalDateTime endTime) {
            this.startTime = startTime;
            this.amount = amount;
            this.chronoUnit = chronoUnit;
            this.endTime = endTime;
        }

        @Override
        public boolean hasNext() {
            return startTime.compareTo(endTime) < 0;
        }

        @Override
        public LocalDateTime next() {
            LocalDateTime copy = LocalDateTime.of(startTime.toLocalDate(), startTime.toLocalTime());
            startTime = startTime.plus(amount, chronoUnit);
            return copy;
        }
    }
}