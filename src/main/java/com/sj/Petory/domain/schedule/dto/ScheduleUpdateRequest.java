package com.sj.Petory.domain.schedule.dto;

import com.sj.Petory.domain.schedule.entity.CustomRepeatPattern;
import com.sj.Petory.domain.schedule.entity.Schedule;
import com.sj.Petory.domain.schedule.type.PriorityType;
import com.sj.Petory.domain.schedule.type.RepeatCycle;
import com.sj.Petory.domain.schedule.type.RepeatType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleUpdateRequest {
    private long categoryId;
    private String title;
    private String content;
    private LocalDateTime scheduleAt;

    private boolean repeatYn;
    private String selectedDates;
    private CustomRepeatRequest customRepeat;

    private boolean noticeYn;
    private long noticeAt;

    private PriorityType priority;

    private List<Long> petId;

    public CustomRepeatPattern toCustomRepeatEntity(
            Schedule schedule) {

        return CustomRepeatPattern.builder()
                .schedule(schedule)
                .frequency(this.customRepeat.getFrequency())
                .repeatInterval(this.customRepeat.getInterval())
                .daysOfWeek(this.customRepeat.getDaysOfWeek())
                .daysOfMonth(this.customRepeat.getDaysOfMonth())
                .endDate(this.customRepeat.getEndDate())
                .build();
    }
}
