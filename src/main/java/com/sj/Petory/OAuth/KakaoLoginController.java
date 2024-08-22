package com.sj.Petory.OAuth;

import com.sj.Petory.domain.member.dto.SignIn;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class KakaoLoginController {
    private final KakaoLoginService kakaoLoginService;

    @GetMapping("/oauth/kakao/callback")//인가코드 발급
    public ResponseEntity<String> callbackKakao(
            @RequestParam("code") String code) {

        System.out.println(code);
        return ResponseEntity.ok(
                kakaoLoginService.getAccessTokenFromKakao(code));
    }

    @PostMapping("/oauth/kakao/extraInfo")
    public ResponseEntity<SignIn.Response> kakaoExtraInfo(
            @RequestHeader("Authorization") String accessToken
            , @RequestBody @Valid ExtraUserInfo extraUserInfo) {

        return ResponseEntity.ok(
                kakaoLoginService.kakaoExtraInfo(accessToken, extraUserInfo));
    }
}
