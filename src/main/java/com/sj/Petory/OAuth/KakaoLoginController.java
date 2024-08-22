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

    @PostMapping("/oauth/kakao/callback")//인가코드 발급
    public ResponseEntity<SignIn.Response> callbackKakao(
            @RequestParam("code") String code
            , @RequestParam("email") String email
            , @RequestParam("phone") String phone) {

        System.out.println(code);
        return ResponseEntity.ok(
                kakaoLoginService.getAccessTokenFromKakao(code, email, phone));

    }
}
