package com.sj.Petory.domain.post.dto;


import com.sj.Petory.domain.member.dto.PostMemberInfo;
import com.sj.Petory.domain.member.dto.PostResponse;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
@Setter
@NoArgsConstructor
public class AllPostResponse {

    //게시글 작성자 정보
    private PostMemberInfo member;

    //게시글 정보
    private PostResponse post;

    private List<PostImageDto> postImageDtoList;

    //댓글 수
    private long commentTotal;
    //공감 수
    private long sympathyTotal;
}
