package com.sj.Petory.domain.schedule.dto;

import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.schedule.entity.ScheduleCategory;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCategoryRequest {

    private String name;

    public ScheduleCategory toEntity(Member member) {

        return ScheduleCategory.builder()
                .member(member)
                .categoryName(this.name)
                .build();
    }
}
