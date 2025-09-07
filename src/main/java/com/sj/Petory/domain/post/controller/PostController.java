package com.sj.Petory.domain.post.controller;

import com.sj.Petory.domain.member.dto.MemberAdapter;
import com.sj.Petory.domain.member.dto.PostResponse;
import com.sj.Petory.domain.post.dto.PostListResponse;
import com.sj.Petory.domain.post.dto.CreatePostRequest;
import com.sj.Petory.domain.post.dto.PostSearchResponse;
import com.sj.Petory.domain.post.dto.UpdatePostRequest;
import com.sj.Petory.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
    public ResponseEntity<List<PostListResponse>> getPostList(Pageable pageable) {

        return ResponseEntity.ok(postService.getPostList(pageable));
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

    @DeleteMapping("/{postId}")
    public ResponseEntity<Boolean> deletePost(
            @PathVariable("postId") long postId
            , @AuthenticationPrincipal MemberAdapter memberAdapter) {

        return ResponseEntity.ok(
                postService.deletePost(
                        postId, memberAdapter));
    }

    @GetMapping(params = "keyword")
    public ResponseEntity<PostSearchResponse> searchPost(
            @RequestParam("keyword") String keyword) throws IOException {

        return ResponseEntity.ok(
                postService.searchPost(keyword));
    }
}
