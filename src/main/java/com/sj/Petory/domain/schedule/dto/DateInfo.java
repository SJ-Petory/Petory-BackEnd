package com.sj.Petory.domain.schedule.dto;

import com.sj.Petory.domain.schedule.entity.SelectDate;
import com.sj.Petory.domain.schedule.type.ScheduleStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DateInfo {

    private LocalDateTime date;
    private ScheduleStatus status;

}
