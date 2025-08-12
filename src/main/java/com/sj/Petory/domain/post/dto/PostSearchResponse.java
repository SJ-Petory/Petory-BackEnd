package com.sj.Petory.domain.post.dto;

import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.post.entity.Post;
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
        private Long id;
        private String title;
        private String content;
        private List<PostImageDto> postImage;
        private long commentTotal;
        private long sympathyTotal;
    }
}
