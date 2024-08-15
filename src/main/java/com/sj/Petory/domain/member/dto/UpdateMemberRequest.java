package com.sj.Petory.domain.member.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateMemberRequest {

    private String name;
    private String password;
    private String image;
}
