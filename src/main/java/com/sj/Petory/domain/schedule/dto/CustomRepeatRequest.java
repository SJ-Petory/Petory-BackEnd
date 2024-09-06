package com.sj.Petory.domain.schedule.dto;

import com.sj.Petory.domain.schedule.entity.CustomRepeatPattern;
import com.sj.Petory.domain.schedule.type.Frequency;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomRepeatRequest {

    private Frequency frequency;

    private Long interval;

    private Set<DayOfWeek> daysOfWeek;
    private Set<Integer> daysOfMonth;
    private LocalDateTime endDate;
}
