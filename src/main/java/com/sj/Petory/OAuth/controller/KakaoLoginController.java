package com.sj.Petory.OAuth.controller;

import com.sj.Petory.OAuth.dto.ExtraUserInfo;
import com.sj.Petory.OAuth.service.KakaoLoginService;
import com.sj.Petory.domain.member.dto.SignIn;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth")
public class KakaoLoginController {
    private final KakaoLoginService kakaoLoginService;

    @GetMapping("/kakao/callback")//인가코드 발급
    public void callbackKakao(
            @RequestParam("code") String code
            , HttpServletResponse response) throws IOException {

        System.out.println(code);
        String redirectUrl = kakaoLoginService.getAccessTokenFromKakao(code);

        response.sendRedirect(redirectUrl);
    }

    @PostMapping("/kakao/extraInfo")
    public ResponseEntity<SignIn.Response> kakaoExtraInfo(
            @RequestBody @Valid ExtraUserInfo extraUserInfo) {

        return ResponseEntity.ok(
                kakaoLoginService.kakaoExtraInfo(extraUserInfo));
    }

    @PostMapping("/token/issue")
    public ResponseEntity<SignIn.Response> issueToken(@RequestBody Map<String, String> request) {

        String code = request.get("code");
        return ResponseEntity.ok(kakaoLoginService.issueToken(code));
    }
}
