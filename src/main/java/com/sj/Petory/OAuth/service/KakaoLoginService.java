package com.sj.Petory.OAuth.service;

import com.sj.Petory.OAuth.dto.ExtraUserInfo;
import com.sj.Petory.OAuth.dto.KakaoTokenResponse;
import com.sj.Petory.OAuth.dto.UserInfoResponse;
import com.sj.Petory.common.es.MemberEsRepository;
import com.sj.Petory.common.s3.AmazonS3Service;
import com.sj.Petory.domain.member.dto.SignIn;
import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.member.repository.MemberRepository;
import com.sj.Petory.domain.member.type.MemberStatus;
import com.sj.Petory.security.JwtUtils;
import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
@Service
@Slf4j
public class KakaoLoginService {

    private final MemberEsRepository memberEsRepository;
    @Value("${kakao.client_id}")
    private String clientId;

    private final String KAUTH_TOKEN_URL_HOST = "https://kauth.kakao.com";
    private final String KAUTH_USER_URL_HOST = "https://kapi.kakao.com";

    private final MemberRepository memberRepository;
    private final JwtUtils jwtUtils;
    private final AmazonS3Service amazonS3Service;

    public String getAccessTokenFromKakao(String code) {

        KakaoTokenResponse kakaoTokenResponseDto =
                WebClient.create(KAUTH_TOKEN_URL_HOST).post()
                        .uri(uriBuilder -> uriBuilder
                                .scheme("https")
                                .path("/oauth/token")
                                .queryParam("grant_type", "authorization_code")
                                .queryParam("client_id", clientId)
                                .queryParam("code", code)
                                .build(true))
                        .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                        .retrieve()
                        //TODO : Custom Exception
//                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
//                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                        .bodyToMono(KakaoTokenResponse.class)
                        .block();


        log.info(" [Kakao Service] Access Token ------> {}", kakaoTokenResponseDto.getAccessToken());
        log.info(" [Kakao Service] Refresh Token ------> {}", kakaoTokenResponseDto.getRefreshToken());
        //제공 조건: OpenID Connect가 활성화 된 앱의 토큰 발급 요청인 경우 또는 scope에 openid를 포함한 추가 항목 동의 받기 요청을 거친 토큰 발급 요청인 경우
        log.info(" [Kakao Service] Id Token ------> {}", kakaoTokenResponseDto.getIdToken());
        log.info(" [Kakao Service] Scope ------> {}", kakaoTokenResponseDto.getScope());

        return kakaoTokenResponseDto.getAccessToken();
    }

    public SignIn.Response kakaoExtraInfo(
            final String accessToken
            , final ExtraUserInfo extraUserInfo) {

        UserInfoResponse userInfoResponse = WebClient.create(KAUTH_USER_URL_HOST).get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/v2/user/me")
                        .build()
                )
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(UserInfoResponse.class)
                .block();

        Member member = memberRepository.save(findOrCreateMember(userInfoResponse, extraUserInfo));
        memberEsRepository.save(member.toDocument());

        return SignIn.Response.toResponse(
                jwtUtils.generateToken(extraUserInfo.getEmail(), "ATK")
                , jwtUtils.generateToken(extraUserInfo.getEmail(), "RTK")
        );
    }

    //findOrCreateMember()
    //존재하면 -> 로그인 성공 ATK, RTK 발급
    //존재하지 않으면 -> 회원 정보 저장
    public Member findOrCreateMember(UserInfoResponse userInfoResponse, ExtraUserInfo extraUserInfo) {
        return memberRepository.findByEmail(extraUserInfo.getEmail())
                .orElseGet(() -> createMember(userInfoResponse, extraUserInfo));
    }

    private Member createMember(
            final UserInfoResponse userInfoResponse
            , final ExtraUserInfo extraUserInfo) {

        UserInfoResponse.KakaoAcount.ProfileInfo profile
                = userInfoResponse.getKakaoAcount().getProfile();

        return Member.builder()
                .name(profile.getNickName())
                .email(extraUserInfo.getEmail())
                .phone(extraUserInfo.getPhone())
                .image(amazonS3Service.uploadImageforKakao(
                        profile.getProfileImageUrl()))
                .status(MemberStatus.ACTIVE)
                .build();
    }
}
