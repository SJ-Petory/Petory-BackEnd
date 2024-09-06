package com.sj.Petory.domain.schedule.dto;

import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.schedule.entity.CustomRepeatPattern;
import com.sj.Petory.domain.schedule.entity.Schedule;
import com.sj.Petory.domain.schedule.entity.ScheduleCategory;
import com.sj.Petory.domain.schedule.type.PriorityType;
import com.sj.Petory.domain.schedule.type.RepeatCycle;
import com.sj.Petory.domain.schedule.type.RepeatType;
import com.sj.Petory.domain.schedule.type.ScheduleStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateScheduleRequest {

    private long categoryId;
    private String title;
    private String content;
    private LocalDateTime scheduleAt;

    private RepeatType repeatType;
    private RepeatCycle repeatCycle;
    private CustomRepeatRequest customRepeat;

    private boolean noticeYn;
    private long noticeAt;

    private PriorityType priority;

    public Schedule toScheduleEntity(
            ScheduleCategory category
            , Member member
            , ScheduleStatus status) {

        return Schedule.builder()
                .scheduleCategory(category)
                .member(member)
                .scheduleTitle(this.title)
                .scheduleContent(this.content)
                .scheduleAt(this.scheduleAt)
                .repeatType(this.repeatType)
                .repeatCycle(this.repeatCycle)
                .noticeYn(this.noticeYn)
                .noticeAt(this.noticeAt)
                .priority(this.priority)
                .status(status)
                .build();
    }

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
