package com.sj.Petory.domain.schedule.service;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class ScheduleAtConverter {

    public static LocalDateTime convertToDateTime(LocalDate date, LocalTime time, boolean isAllDay) {
        if (date == null) {
            throw new IllegalArgumentException("Date must not be null");
        }

        if (isAllDay || time == null) {
            return date.atStartOfDay();
        }

        return date.atTime(time);
    }

    public static LocalDateTime convertToDateTime(LocalDateTime dateTime, LocalTime time) {
        if (dateTime == null || time == null) {
            throw new IllegalArgumentException("DateTime must not be null");
        }
        return dateTime
                .withHour(time.getHour())
                .withMinute(time.getMinute())
                .withSecond(time.getSecond())
                .withNano(time.getNano());
    }

    public static LocalDate convertToDate(int year, int month, int day) {

        try {
            return LocalDate.of(year, month, day);
        } catch (DateTimeException e) {
            return null;
        }
    }

    public static LocalDate convertToDate(LocalDate date) {

        try {
            return LocalDate.of(
                    date.getYear(), date.getMonth(), date.getDayOfMonth());
        } catch (DateTimeException e) {
            return null;
        }
    }
}
