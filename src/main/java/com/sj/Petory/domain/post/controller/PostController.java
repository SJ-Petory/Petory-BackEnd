package com.sj.Petory.domain.post.controller;

import com.sj.Petory.domain.member.dto.MemberAdapter;
import com.sj.Petory.domain.post.dto.AllPostResponse;
import com.sj.Petory.domain.post.dto.CreatePostRequest;
import com.sj.Petory.domain.post.dto.UpdatePostRequest;
import com.sj.Petory.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community/posts")
public class PostController {

    private final PostService postService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Boolean> createPost(
            @AuthenticationPrincipal MemberAdapter memberAdapter,
            @ModelAttribute CreatePostRequest createPostRequest) {

        return ResponseEntity.ok(postService.createPost(memberAdapter, createPostRequest));
    }

    @GetMapping
    public ResponseEntity<List<AllPostResponse>> getPostList() {

        return ResponseEntity.ok(postService.getPostList());
    }

    @PatchMapping(path = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Boolean> updatePost(
            @PathVariable("postId") long postId
            , @ModelAttribute UpdatePostRequest request
            , @AuthenticationPrincipal MemberAdapter memberAdapter) {

        return ResponseEntity.ok(
                postService.updatePost(
                        request, postId, memberAdapter));
    }
}
