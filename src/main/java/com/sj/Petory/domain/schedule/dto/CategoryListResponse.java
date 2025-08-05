package com.sj.Petory.domain.schedule.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryListResponse {

    private Long categoryId;
    private String name;
}
