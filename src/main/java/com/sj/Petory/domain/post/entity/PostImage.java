package com.sj.Petory.domain.post.entity;

import com.sj.Petory.domain.post.dto.PostImageDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "postimage")
public class PostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long postImageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "image_url")
    private String imageUrl;

    public PostImageDto toDto() {

        return new PostImageDto(
                postImageId, imageUrl);
    }
}
