package com.sj.Petory.OAuth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KakaoLoginController {
    private final KakaoLoginService kakaoLoginService;

    @GetMapping("/oauth/kakao/callback")//인가코드 발급
    public ResponseEntity<?> callbackKakao(@RequestParam("code") String code) {

        System.out.println(code);
        return ResponseEntity.ok(kakaoLoginService.getAccessTokenFromKakao(code));

    }
}
