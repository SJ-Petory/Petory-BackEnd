package com.sj.Petory.domain.schedule.dto;

import com.sj.Petory.domain.schedule.type.PriorityType;
import com.sj.Petory.domain.schedule.type.ScheduleStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleListResponse {

    private Long scheduleId;
    private String title;
    private LocalDateTime scheduleAt;
    private PriorityType priority;
    private ScheduleStatus status;
    private List<Long> petId;
    private List<String> petName;
}
