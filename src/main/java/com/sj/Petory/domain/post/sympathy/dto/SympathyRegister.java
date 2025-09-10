package com.sj.Petory.domain.post.sympathy.dto;

import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.post.entity.Post;
import com.sj.Petory.domain.post.sympathy.entity.Sympathy;
import com.sj.Petory.domain.post.sympathy.type.SympathyType;
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
