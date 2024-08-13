package com.sj.Petory.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

public class SignIn {

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {

        @NotBlank
        private String email;

        @NotBlank
        private String password;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {

        private String accessToken;
        private String refreshToken;

        public static Response toResponse(String accessToken, String refreshToken) {

            return new Response(accessToken, refreshToken);
        }
    }
}
