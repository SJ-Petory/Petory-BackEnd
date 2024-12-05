package com.sj.Petory.domain.schedule.dto;

import com.sj.Petory.domain.schedule.type.PriorityType;
import com.sj.Petory.domain.schedule.type.ScheduleStatus;
import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleDetailResponse {

    private Long categoryId;
    private String categoryName;
    private Long scheduleId;
    private String title;
    private String content;

    private boolean noticeYn;
    private long noticeAt;

    private PriorityType priority;
    private ScheduleStatus status;

    private List<Long> petId;
    private List<String> petName;

    private boolean isAllDay;
    private LocalTime scheduleAt;

    private boolean repeatYn;
    private RepeatPatternDto.Response repeatPattern;
    private List<String> selectedDates;
}
