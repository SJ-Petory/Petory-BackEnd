package com.sj.Petory.domain.post.comment.dto;

import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.post.comment.entity.Comment;
import com.sj.Petory.domain.post.comment.type.CommentStatus;
import com.sj.Petory.domain.post.entity.Post;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRegisterRequest {

    private Long postId;
    private String content;

    public Comment toEntity(Post post, Member member) {

        return Comment.builder()
                .post(post)
                .member(member)
                .content(this.getContent())
                .status(CommentStatus.ACTIVE)
                .build();
    }
}
