package com.sj.Petory.OAuth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfoResponse {
    @JsonProperty("id")
    private long id;

    @JsonProperty("properties")
    private HashMap<String, String> properties;

    @JsonProperty("kakao_account")
    private KakaoAcount kakaoAcount;

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class KakaoAcount {

        @JsonProperty("profile")
        public ProfileInfo profile;

        @JsonProperty("profile_nickname_needs_agreement")
        public boolean nickNameAgree;

        @JsonProperty("profile_image_needs_agreement")
        public boolean profileImageAgree;

        @Getter
        @NoArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public class ProfileInfo {

            @JsonProperty("nickname")
            public String nickName;

            @JsonProperty("profile_image_url")
            public String profileImageUrl;
        }
    }
}
