package com.sj.Petory.domain.member.controller;

import com.sj.Petory.domain.member.dto.SignUp;
import com.sj.Petory.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<Boolean> signUp(
            @RequestBody @Valid SignUp.Request request) {

        return ResponseEntity.ok(
                memberService.signUp(request));
    }

    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmailDuplicate(
            @RequestParam("email") String email) {

        return ResponseEntity.ok(
                memberService.checkEmailDuplicate(email)
        );
    }
}
