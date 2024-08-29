package com.sj.Petory.domain.friend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberSearchResponse {

    private Long id;
    private String name;
    private String image;
}
