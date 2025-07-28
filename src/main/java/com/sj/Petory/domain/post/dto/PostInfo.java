package com.sj.Petory.domain.post.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostInfo {

    private long postId;
    private String title;
    private String content;
}
