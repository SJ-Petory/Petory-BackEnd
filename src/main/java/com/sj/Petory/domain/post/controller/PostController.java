package com.sj.Petory.domain.post.controller;

import com.sj.Petory.domain.member.dto.MemberAdapter;
import com.sj.Petory.domain.post.dto.CreatePostRequest;
import com.sj.Petory.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
public class PostController {

    private final PostService postService;

    @PostMapping("/posts")
    public ResponseEntity<Boolean> createPost(
            @AuthenticationPrincipal MemberAdapter memberAdapter,
            @RequestBody CreatePostRequest createPostRequest) {

        return ResponseEntity.ok(postService.createPost(memberAdapter, createPostRequest));
    }
}
