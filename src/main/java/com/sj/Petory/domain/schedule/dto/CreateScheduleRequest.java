package com.sj.Petory.domain.schedule.dto;

import lombok.*;

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
    private boolean repeatYn;
    private List<String> schedulesDates;
    private boolean noticeYn;
    private long noticeAt;
    private List<Long> petId;

    private RepeatPatternRequest repeatPattern;
}
