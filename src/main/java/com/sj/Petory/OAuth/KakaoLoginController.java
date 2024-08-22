package com.sj.Petory.OAuth;

import com.sj.Petory.domain.member.dto.SignIn;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class KakaoLoginController {
    private final KakaoLoginService kakaoLoginService;

    @GetMapping("/oauth/kakao/callback")//인가코드 발급
    public ResponseEntity<Void> callbackKakao(
            @RequestParam("code") String code
            , HttpServletResponse response) throws IOException {

        System.out.println(code);
        String accessTokenFromKakao = kakaoLoginService.getAccessTokenFromKakao(code);

        String redirectUrl = "http://43.202.195.199/inputInfo?token=" + accessTokenFromKakao;

        response.sendRedirect(redirectUrl);
    }

    @PostMapping("/oauth/kakao/extraInfo")
    public ResponseEntity<SignIn.Response> kakaoExtraInfo(
            @RequestHeader("Authorization") String accessToken
            , @RequestBody @Valid ExtraUserInfo extraUserInfo) {

        return ResponseEntity.ok(
                kakaoLoginService.kakaoExtraInfo(accessToken, extraUserInfo));
    }
}
