package com.sj.Petory.domain.post.dto;

import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.post.entity.Post;
import com.sj.Petory.domain.post.entity.PostCategory;
import com.sj.Petory.domain.post.type.PostStatus;
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

    public Post toEntity(Member member, PostCategory postCategory) {
        return Post.builder()
                .member(member)
                .postCategory(postCategory)
                .postTitle(this.getTitle())
                .postContent(this.getContent())
                .status(PostStatus.ACTIVE)
                .build();
    }
}