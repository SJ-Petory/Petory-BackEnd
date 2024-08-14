package com.sj.Petory.domain.member.dto;

import com.sj.Petory.domain.member.entity.Member;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberInfoResponse {
    private String name;
    private String phone;
    private String image;

    public static MemberInfoResponse fromEntity(Member member) {
        return MemberInfoResponse.builder()
                .name(member.getName())
                .phone(member.getPhone())
                .build();
    }
}
