package com.sj.Petory.domain.post.entity;

import com.sj.Petory.domain.member.dto.PostResponse;
import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.post.type.PostStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long postId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "post_category_id")
    private PostCategory postCategory;

    @Column
    private String postTitle;

    @Column
    private String postContent;

    @Enumerated(EnumType.STRING)
    private PostStatus status;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column
    private LocalDateTime updatedAt;

    public PostResponse toDto() {
        return PostResponse.builder()
                .postId(this.postId)
                .title(this.postTitle)
                .content(this.postContent)
                .build();
    }
}
