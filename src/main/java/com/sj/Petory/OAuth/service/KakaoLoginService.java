package com.sj.Petory.OAuth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sj.Petory.OAuth.dto.*;
import com.sj.Petory.OAuth.type.SocialType;
import com.sj.Petory.common.es.MemberEsRepository;
import com.sj.Petory.common.s3.AmazonS3Service;
import com.sj.Petory.domain.member.dto.SignIn;
import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.member.repository.MemberRepository;
import com.sj.Petory.domain.member.type.MemberStatus;
import com.sj.Petory.domain.member.type.Role;
import com.sj.Petory.exception.OAuthException;
import com.sj.Petory.exception.type.ErrorCode;
import com.sj.Petory.security.JwtUtils;
import io.netty.handler.codec.http.HttpHeaderValues;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
@Slf4j
public class KakaoLoginService {

    private final MemberEsRepository memberEsRepository;
    @Value("${kakao.client_id}")
    private String clientId;

    @Value("${app.front-url}") // 설정 파일에서 주소 가져오기
    private String frontUrl;

    private final static String KAUTH_TOKEN_URL_HOST = "https://kauth.kakao.com";
    private final static String KAUTH_USER_URL_HOST = "https://kapi.kakao.com";

    private final MemberRepository memberRepository;
    private final JwtUtils jwtUtils;
    private final AmazonS3Service amazonS3Service;
    private final RedisTemplate<String, Object> redisTemplate; // Redis 주입!

    public String getAccessTokenFromKakao(String code) throws JsonProcessingException {

        TokenResponseFromKakao tokenResponse =
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
                        .bodyToMono(TokenResponseFromKakao.class)
                        .block();


        if (tokenResponse == null) {
            throw new RuntimeException("카카오 응답이 없습니다.");
        }

        String idToken = tokenResponse.getIdToken();
        // 1. 토큰 디코딩
        String[] token = idToken.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payloadJson = new String(decoder.decode(token[1]));

        // 2. Payload 값 추출
        ObjectMapper mapper = new ObjectMapper();

        @SuppressWarnings("unchecked")
        Map<String, Object> claims = mapper.readValue(payloadJson, Map.class);

        String sub = (String) claims.get("sub");

        System.out.println("회원번호(sub): " + sub);

        Optional<Member> newMember = memberRepository.findByProviderId(sub);

        String registerId = UUID.randomUUID().toString();

        if (newMember.isPresent()) {

            Member member = newMember.get();
            String accessToken = jwtUtils.generateToken(member.getEmail(), "ATK", member.getRole().getKey());
            String refreshToken = jwtUtils.generateToken(member.getEmail(), "RTK", member.getRole().getKey());

            TokenInfo tokenInfo = new TokenInfo(accessToken, refreshToken);

            redisTemplate.opsForValue().set(
                    registerId,
                    tokenInfo,
                    30,
                    TimeUnit.MINUTES
            );

            return UriComponentsBuilder.fromUriString(frontUrl + "/mainPage")
                    .queryParam("code", registerId)
                    .queryParam("status", "login")
                    .build().toUriString();
        } else { //존재하지 않으면 기존 회원과 연결 or 회원가입 로직

            UserInfoResponse userInfoResponse = WebClient.create(KAUTH_USER_URL_HOST).get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .path("/v2/user/me")
                            .build()
                    )
                    .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                    .header("Authorization", "Bearer " + tokenResponse.getAccessToken())
                    .retrieve()
                    .bodyToMono(UserInfoResponse.class)
                    .block();

            if (userInfoResponse == null) {
                throw new RuntimeException("카카오 유저 정보를 가져오지 못했습니다.");
            }

            UserInfoResponse.KakaoAcount.ProfileInfo profile
                    = userInfoResponse.getKakaoAcount().getProfile();

            CachedKakaoInfo cachedInfo = CachedKakaoInfo.builder()
                    .sub(sub)
                    .name(profile.getNickName())
                    .image(amazonS3Service.uploadImageforKakao(
                            profile.getProfileImageUrl()))
                    .build();

            redisTemplate.opsForValue().set(
                    registerId,
                    cachedInfo,
                    30,
                    TimeUnit.MINUTES
            );
            return UriComponentsBuilder.fromUriString(frontUrl + "/oauth/kakao/callback")
                    .queryParam("registerId", registerId)
                    .queryParam("status", "register")
                    .build().toUriString();
        }
    }

    @Transactional
    public SignIn.Response kakaoExtraInfo(
            final ExtraUserInfo extraUserInfo) {

        String key = extraUserInfo.getRegisterId();

        CachedKakaoInfo kakaoInfo = (CachedKakaoInfo) redisTemplate.opsForValue().get(key);

        if (kakaoInfo == null) {
            throw new OAuthException(ErrorCode.INVALID_REGISTER_KEY);
        }

        if (memberRepository.existsByProviderId(kakaoInfo.getSub())) {
            throw new OAuthException(ErrorCode.OAUTH_MEMBER_DUPLICATED);
        }

        Member member = memberRepository.save(findOrCreateMember(extraUserInfo, kakaoInfo));
        member.updateProviderInfo(SocialType.KAKAO, kakaoInfo.getSub()); // 소셜 정보 업데이트

        memberEsRepository.save(member.toDocument());

        redisTemplate.delete(key);

        return SignIn.Response.toResponse(
                jwtUtils.generateToken(extraUserInfo.getEmail(), "ATK", member.getRole().getKey())
                , jwtUtils.generateToken(extraUserInfo.getEmail(), "RTK", member.getRole().getKey())
        );
    }

    public Member findOrCreateMember(ExtraUserInfo extraUserInfo, CachedKakaoInfo kakaoInfo) {
        return memberRepository.findByEmail(extraUserInfo.getEmail())
                .orElseGet(() -> createMember(extraUserInfo, kakaoInfo));
    }

    private Member createMember(ExtraUserInfo extraUserInfo, CachedKakaoInfo kakaoInfo) {

        return Member.builder()
                .email(extraUserInfo.getEmail())
                .phone(extraUserInfo.getPhone())
                .name(kakaoInfo.getName()) // Redis에 있던 닉네임 사용
                .image(amazonS3Service.uploadImageforKakao(
                        kakaoInfo.getImage()))
                .status(MemberStatus.ACTIVE)
                .role(Role.USER)
                .build();
    }

    public SignIn.Response issueToken(String code) {

        Object data = redisTemplate.opsForValue().get(code);

        if (data == null) {
            throw new OAuthException(ErrorCode.INVALID_CODE);
        }

        TokenInfo tokenInfo;
        try {
            ObjectMapper mapper = new ObjectMapper();
            tokenInfo = mapper.convertValue(data, TokenInfo.class);
        } catch (IllegalArgumentException e) {
            throw new OAuthException(ErrorCode.INVALID_OAUTH_DATA);
        }

        redisTemplate.delete(code);

        return SignIn.Response.toResponse(tokenInfo.accessToken(), tokenInfo.refreshToken());
    }
}
