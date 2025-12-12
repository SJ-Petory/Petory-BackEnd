package com.sj.Petory.domain.post.controller;

import com.sj.Petory.domain.member.dto.MemberAdapter;
import com.sj.Petory.domain.post.dto.CreatePostCategoryRequest;
import com.sj.Petory.domain.post.dto.PostCategoryResponse;
import com.sj.Petory.domain.post.service.PostCategoryService;
import com.sj.Petory.domain.schedule.dto.CreateScheduleCategoryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community/category")
public class PostCategoryController {

    private final PostCategoryService postCategoryService;

    @GetMapping
    public ResponseEntity<List<PostCategoryResponse>> getCategory() {

        return ResponseEntity.ok(postCategoryService.getCategory());
    }


    @PostMapping()
    public ResponseEntity<Boolean> createPostCategory(
            @AuthenticationPrincipal MemberAdapter memberAdapter
            , @RequestBody CreatePostCategoryRequest request) {

        return ResponseEntity.ok(postCategoryService.createPostCategory(
                memberAdapter, request));
    }
}
