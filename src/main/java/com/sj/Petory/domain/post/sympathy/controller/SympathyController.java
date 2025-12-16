package com.sj.Petory.domain.post.sympathy.controller;

import com.sj.Petory.domain.member.dto.MemberAdapter;
import com.sj.Petory.domain.post.sympathy.dto.PostSympathiesResponse;
import com.sj.Petory.domain.post.sympathy.dto.SympathyRegister;
import com.sj.Petory.domain.post.sympathy.sevice.SympathyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community/sympathy")
public class SympathyController {

    private final SympathyService sympathyService;

    @PostMapping("/{postId}")
    public ResponseEntity<Boolean> sympathyRegister(
            @AuthenticationPrincipal MemberAdapter memberAdapter
            , @PathVariable("postId") Long postId
            , @RequestBody SympathyRegister request) {

        return ResponseEntity.ok(
                sympathyService.sympathyRegister(
                        memberAdapter, postId, request));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<List<PostSympathiesResponse>> getSympathies(
            @AuthenticationPrincipal MemberAdapter memberAdapter
            , @PathVariable("postId") Long postId) {

        return ResponseEntity.ok(sympathyService.getSympathies(
                memberAdapter, postId));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Boolean> deleteSympathy(
            @AuthenticationPrincipal MemberAdapter memberAdapter
            , @PathVariable("postId") Long postId) {

        return ResponseEntity.ok(
                sympathyService.deleteSympathy(memberAdapter, postId));
    }

}
