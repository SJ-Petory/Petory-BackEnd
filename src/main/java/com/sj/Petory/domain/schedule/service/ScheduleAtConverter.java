package com.sj.Petory.domain.schedule.service;

import org.springframework.stereotype.Component;

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
}
