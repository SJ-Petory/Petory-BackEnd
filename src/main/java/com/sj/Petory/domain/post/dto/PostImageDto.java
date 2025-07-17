package com.sj.Petory.domain.post.dto;

import com.sj.Petory.domain.post.entity.PostImage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostImageDto {

    private String imageUrl;

    public PostImageDto(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public PostImage toEntity() {
        return PostImage.builder()
                .imageUrl(this.imageUrl)
                .build();
    }
}
