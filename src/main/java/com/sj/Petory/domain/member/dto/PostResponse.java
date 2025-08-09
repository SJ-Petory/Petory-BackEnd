package com.sj.Petory.domain.member.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponse {
    private long categoryId;
    private long postId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
}
