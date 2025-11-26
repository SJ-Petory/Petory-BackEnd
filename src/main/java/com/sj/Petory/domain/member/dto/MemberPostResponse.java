package com.sj.Petory.domain.member.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberPostResponse {
    private PostMemberInfo member;

    private List<PostResponse> posts;
}

