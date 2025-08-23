package com.sj.Petory.domain.post.dto;

import com.sj.Petory.domain.post.entity.PostImage;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostSearchResponse {

    private List<PostWrapper> posts;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostWrapper {
        private Member member;
        private Post post;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Member {
        private Long id;
        private String image;
        private String name;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Post {
        private Long categoryId;
        private Long postId;
        private String title;
        private String content;
        private List<PostImageDto> postImage;
        private long commentTotal;
        private long sympathyTotal;
    }

    public static PostSearchResponse.Member toMemberResponse(
            com.sj.Petory.domain.member.entity.Member memberEntity) {

        return PostSearchResponse.Member.builder()
                .id(memberEntity.getMemberId())
                .name(memberEntity.getName())
                .image(memberEntity.getImage())
                .build();
    }

    public static PostSearchResponse.Post toPostResponse(
            com.sj.Petory.domain.post.entity.Post postEntity) {

        return Post.builder()
                .categoryId(postEntity.getPostCategory().getPostCategoryId())
                .postId(postEntity.getPostId())
                .title(postEntity.getPostTitle())
                .content(postEntity.getPostContent())
                .postImage(postEntity.getPostImageList().stream().map(PostImage::toDto).toList())
                .build();
    }
}
