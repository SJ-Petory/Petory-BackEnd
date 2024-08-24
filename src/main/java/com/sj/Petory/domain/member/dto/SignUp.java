package com.sj.Petory.domain.member.dto;

import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.member.type.MemberStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

public class SignUp {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {

        @NotBlank
        private String name;

        @NotBlank
        @Email
        private String email;

        @NotBlank
        private String password;

        @NotBlank
        private String phone;

        public Member toEntity() {
            return Member.builder()
                    .name(this.name)
                    .email(this.email)
                    .password(this.password)
                    .phone(this.phone)
                    .status(MemberStatus.ACTIVE)
                    .build();
        }
    }
}
