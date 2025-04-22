package com.sj.Petory.domain.post.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePostRequest {

    private Long categoryId;
    private String title;
    private String content;
    private String imageUrl;
}