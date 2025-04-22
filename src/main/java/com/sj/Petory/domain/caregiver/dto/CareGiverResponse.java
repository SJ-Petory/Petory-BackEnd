package com.sj.Petory.domain.caregiver.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CareGiverResponse {

    private Long memberId;
    private String memberName;
}
