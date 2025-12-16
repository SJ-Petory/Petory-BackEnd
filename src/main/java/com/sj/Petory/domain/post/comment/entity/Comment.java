package com.sj.Petory.domain.post.comment.entity;

import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.post.comment.dto.PostCommentsResponse;
import com.sj.Petory.domain.post.comment.type.CommentStatus;
import com.sj.Petory.domain.post.entity.Post;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private long commentId;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "content")
    private String content;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CommentStatus status;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column
    private LocalDateTime updatedAt;

    public void softDelete() {

        this.status = CommentStatus.DELETED;
    }

    public void updateContent(String content) {

        this.content = content;
    }

    public PostCommentsResponse toDto() {

        return PostCommentsResponse.builder()
                .commentId(this.getCommentId())
                .memberId(this.member.getMemberId())
                .memberName(this.member.getName())
                .memberImage(this.member.getImage())
                .content(this.getContent())
                .createdAt(this.getCreatedAt())
                .build();
    }
}
