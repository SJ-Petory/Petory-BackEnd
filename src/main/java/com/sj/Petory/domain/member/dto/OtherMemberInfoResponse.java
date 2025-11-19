package com.sj.Petory.domain.member.dto;

import com.sj.Petory.domain.member.entity.Member;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtherMemberInfoResponse {
    private String name;
    private String email;
    private String phone;
    private String image;

    public static OtherMemberInfoResponse fromEntity(Member otherMember) {

        return OtherMemberInfoResponse.builder()
                .email(otherMember.getEmail())
                .name(otherMember.getName())
                .phone(otherMember.getPhone())
                .image(otherMember.getImage())
                .build();
    }
}
