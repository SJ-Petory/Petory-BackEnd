package com.sj.Petory.domain.schedule.dto;

import com.sj.Petory.domain.schedule.type.Frequency;
import lombok.*;

import java.time.DayOfWeek;
import java.util.Set;

public class RepeatPatternDto {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private Frequency frequency;

        private Long interval;

        private Set<DayOfWeek> daysOfWeek;
        private Set<Integer> daysOfMonth;

        private String startDate;
        private String endDate;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Frequency frequency;

        private Long interval;

        private Set<DayOfWeek> daysOfWeek;
        private Set<Integer> daysOfMonth;

        private String startDate;
        private String endDate;
    }
}
