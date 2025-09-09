package com.sj.Petory.domain.post.sympathy;

import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.post.entity.Post;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SympathyRegister {

    private String type;

    public Sympathy toEntity(Post post, Member member, SympathyType type) {

        return Sympathy.builder()
                .post(post)
                .member(member)
                .type(type)
                .build();
    }
}
