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

import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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


        log.info(" [Kakao Service] Access Token ------> {}", tokenResponse.getAccessToken());
        log.info(" [Kakao Service] Refresh Token ------> {}", tokenResponse.getRefreshToken());
        //제공 조건: OpenID Connect가 활성화 된 앱의 토큰 발급 요청인 경우 또는 scope에 openid를 포함한 추가 항목 동의 받기 요청을 거친 토큰 발급 요청인 경우
        log.info(" [Kakao Service] Id Token ------> {}", tokenResponse.getIdToken());
        log.info(" [Kakao Service] Scope ------> {}", tokenResponse.getScope());

        String idToken = tokenResponse.getIdToken();
        //토큰 파싱
        // 1. 토큰 디코딩
        String[] token = idToken.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payloadJson = new String(decoder.decode(token[1]));

        System.out.println("디코딩 된 JSON : " + payloadJson);

        // 2. Payload 값 추출
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> claims = mapper.readValue(payloadJson, Map.class);

        String sub = (String) claims.get("sub");

        System.out.println("회원번호(sub): " + sub);

        Optional<Member> newMember = memberRepository.findByProviderId(sub);

        if (newMember.isPresent()) { //이미 존재하면
            //로그인 완 토큰 발급
            Member member = newMember.get();
            String accessToken = jwtUtils.generateToken(member.getEmail(), "ATK", member.getRole().getKey());
            String refreshToken = jwtUtils.generateToken(member.getEmail(), "RTK", member.getRole().getKey());

            return UriComponentsBuilder.fromUriString("/mainPage")
                    .queryParam("accessToken", accessToken)
                    .queryParam("refreshToken", refreshToken)
                    .build().toUriString();
        } else { //존재하지 않으면 기존 회원과 연결 or 회원가입 로직
            //을 할려면 추가 정보(이메일, 폰번호)가 있어야 한다
            String registerId = UUID.randomUUID().toString();

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
            return UriComponentsBuilder.fromUriString("https://petory.site/inputInfo")
                    .queryParam("registerId", registerId)
                    .build().toUriString();
        }
    }

    @Transactional
    public SignIn.Response kakaoExtraInfo(
            final ExtraUserInfo extraUserInfo) {

        //regester로 map에서 임시 저장된 데이터들 가져옴
        //뭐머ㅜ 저장했냐면 식별자(sub), 이름, 이미지

        String key = extraUserInfo.getRegisterId();
        CachedKakaoInfo kakaoInfo = (CachedKakaoInfo) redisTemplate.opsForValue().get(key);

        if (kakaoInfo == null) {
            throw new RuntimeException("유효시간이 만료되었거나 잘못된 요청입니다.");
        }

        Member member = memberRepository.save(findOrCreateMember(extraUserInfo, kakaoInfo));
        member.updateProviderInfo(SocialType.KAKAO, kakaoInfo.getSub()); // 소셜 정보 업데이트

        memberEsRepository.save(member.toDocument());

        // 3. 사용한 임시 데이터 삭제 (선택 사항 - TTL 있어서 굳이 안 해도 됨)
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
}
