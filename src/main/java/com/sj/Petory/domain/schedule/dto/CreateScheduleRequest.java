package com.sj.Petory.domain.schedule.dto;

import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.pet.entity.Pet;
import com.sj.Petory.domain.schedule.entity.PetSchedule;
import com.sj.Petory.domain.schedule.entity.RepeatPattern;
import com.sj.Petory.domain.schedule.entity.Schedule;
import com.sj.Petory.domain.schedule.entity.ScheduleCategory;
import com.sj.Petory.domain.schedule.type.PriorityType;
import com.sj.Petory.domain.schedule.type.ScheduleStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateScheduleRequest {

    private long categoryId;
    private String title;
    private String content;

    @NotNull
    private Boolean isAllDay;
    private LocalTime scheduleAt;

    @NotNull
    private Boolean repeatYn;

    private List<String> selectedDates;
    private boolean noticeYn;
    private long noticeAt;
    private String priority;

    private List<Long> petId;

    private RepeatPatternRequest repeatPattern;

    public Schedule toScheduleEntity(Member member, ScheduleCategory scheduleCategory) {

        return Schedule.builder()
                .scheduleCategory(scheduleCategory)
                .member(member)
                .scheduleTitle(this.getTitle())
                .scheduleContent(this.getContent())
                .isAllDay(this.getIsAllDay())
                .repeatYn(this.getRepeatYn())
                .noticeYn(this.isNoticeYn())
                .noticeAt(this.getNoticeAt())
                .priority(PriorityType.valueOf(this.getPriority()))
                .status(ScheduleStatus.ONGOING)
                .build();
    }

    public RepeatPattern toRepeatPatternEntity(
            Schedule schedule) {

        return RepeatPattern.builder()
                .schedule(schedule)
                .frequency(this.repeatPattern.getFrequency())
                .repeatInterval(this.repeatPattern.getInterval())
                .daysOfWeek(this.repeatPattern.getDaysOfWeek())
                .daysOfMonth(this.repeatPattern.getDaysOfMonth())
                .startDate(LocalDateTime.parse(this.repeatPattern.getStartDate()))
                .endDate(LocalDateTime.parse(this.repeatPattern.getEndDate()))
                .build();
    }

    public PetSchedule toPetScheduleEntity(Pet pet, Schedule schedule) {

        return PetSchedule.builder()
                .pet(pet)
                .schedule(schedule)
                .build();
    }
}
