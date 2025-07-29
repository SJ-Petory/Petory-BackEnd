package com.sj.Petory.domain.member.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PostMemberInfo {

    private long id;
    private String image;
    private String name;
}
