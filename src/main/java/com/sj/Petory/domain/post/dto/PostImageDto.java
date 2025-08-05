package com.sj.Petory.domain.post.dto;

import com.sj.Petory.domain.post.entity.PostImage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostImageDto {

    private long imageId;
    private String imageUrl;

    public PostImageDto(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public PostImageDto(long imageId, String imageUrl) {
        this.imageId = imageId;
        this.imageUrl = imageUrl;
    }

    public PostImage toEntity() {
        return PostImage.builder()
                .imageUrl(this.imageUrl)
                .build();
    }
}
