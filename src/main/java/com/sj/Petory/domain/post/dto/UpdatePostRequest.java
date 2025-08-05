package com.sj.Petory.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePostRequest {

    private Long categoryId;
    private String title;
    private String content;

    private List<Long> deleteImageIds = new ArrayList<>();
    private List<MultipartFile> newImages = new ArrayList<>();
}
