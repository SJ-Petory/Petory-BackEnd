package com.sj.Petory.domain.member.controller;

import com.sj.Petory.domain.member.dto.*;
import com.sj.Petory.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/check-name")
    public ResponseEntity<Boolean> checkNameDuplicate(
            @RequestParam("name") String name) {

        return ResponseEntity.ok(
                memberService.checkNameDuplicate(name)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<SignIn.Response> signIn(
            @RequestBody @Valid SignIn.Request request) {

        return ResponseEntity.ok(memberService.signIn(request));
    }

    @GetMapping
    public ResponseEntity<MemberInfoResponse> getMembers(
            @AuthenticationPrincipal MemberAdapter memberAdapter) {

        return ResponseEntity.ok(memberService.getMembers(memberAdapter));
    }

    @GetMapping("/pets")
    public ResponseEntity<Page<PetResponse>> getMembersPets(
            @AuthenticationPrincipal MemberAdapter memberAdapter
            , Pageable pageable) {

        return ResponseEntity.ok(
                memberService.getMembersPets(
                        memberAdapter, pageable));
    }

    @GetMapping("/posts")
    public ResponseEntity<Page<PostResponse>> getMembersPosts(
            @AuthenticationPrincipal MemberAdapter memberAdapter
            , Pageable pageable) {

        return ResponseEntity.ok(
                memberService.getMembersPosts(
                        memberAdapter, pageable));
    }

    @GetMapping("/test")
    public String test() {
        return "hello";
    }
}
