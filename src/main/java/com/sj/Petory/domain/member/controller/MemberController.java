package com.sj.Petory.domain.member.controller;

import com.sj.Petory.domain.member.dto.SignUp;
import com.sj.Petory.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<Boolean> signUp(@RequestBody SignUp.Request request) {

        return ResponseEntity.ok(memberService.signUp(request));
    }
}
