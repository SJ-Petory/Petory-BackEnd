package com.sj.Petory.domain.post.entity;

import com.sj.Petory.domain.member.dto.PostResponse;
import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.post.dto.UpdatePostRequest;
import com.sj.Petory.domain.post.type.PostStatus;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "post")
@DynamicUpdate
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private long postId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "post_category_id")
    private PostCategory postCategory;

    @Column(name = "post_title")
    private String postTitle;

    @Column(name = "post_content")
    private String postContent;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> postImageList = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PostStatus status;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column
    private LocalDateTime updatedAt;

    public PostResponse toDto() {
        return PostResponse.builder()
                .categoryId(this.getPostCategory().getPostCategoryId())
                .postId(this.getPostId())
                .title(this.getPostTitle())
                .content(this.getPostContent())
                .createdAt(this.getCreatedAt())
                .build();
    }
    public void clearPostImages() {
        for (PostImage image : postImageList) {
            image.setPost(null);
        }
        postImageList.clear();
    }

    public void addPostImage(PostImage image) {
        postImageList.add(image);
    }

    public void update(UpdatePostRequest request) {
        if (!StringUtils.isEmpty(request.getTitle())) {
            this.setPostTitle(request.getTitle());
        }
        if (!StringUtils.isEmpty(request.getContent())) {
            this.setPostContent(request.getContent());
        }
    }
}
