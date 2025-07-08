package com.sj.Petory.domain.post.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePostRequest {

    private Long categoryId;
    private String title;
    private String content;
    private List<MultipartFile> image;
}