package com.sj.Petory.domain.member.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponse {
    private long postId;
    private String title;
    private String content;
}
