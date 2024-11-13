package com.sj.Petory.domain.schedule.dto;

import com.sj.Petory.domain.schedule.entity.CustomRepeatPattern;
import com.sj.Petory.domain.schedule.type.PriorityType;
import com.sj.Petory.domain.schedule.type.RepeatCycle;
import com.sj.Petory.domain.schedule.type.RepeatType;
import com.sj.Petory.domain.schedule.type.ScheduleStatus;
import lombok.*;

import java.time.LocalDateTime;
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
    private LocalDateTime scheduleAt;

    private boolean repeatYn;
    private String scheduleDates;
    private CustomRepeatResponse customRepeat;

    private boolean noticeYn;
    private long noticeAt;

    private PriorityType priority;

    private ScheduleStatus status;

    private List<Long> petId;
    private List<String> petName;

    public void customRepeatRes(CustomRepeatPattern customRepeatPattern) {

        this.customRepeat =
                new CustomRepeatResponse(
                        customRepeatPattern.getFrequency()
                        , customRepeatPattern.getRepeatInterval()
                        , customRepeatPattern.getDaysOfWeek()
                        , customRepeatPattern.getDaysOfMonth()
                        , customRepeatPattern.getEndDate());
    }
}
