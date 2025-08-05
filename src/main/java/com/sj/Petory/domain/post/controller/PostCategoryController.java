package com.sj.Petory.domain.post.controller;

import com.sj.Petory.domain.post.dto.PostCategoryResponse;
import com.sj.Petory.domain.post.service.PostCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community/category")
public class PostCategoryController {

    private final PostCategoryService postCategoryService;

    @GetMapping
    public ResponseEntity<List<PostCategoryResponse>> getCategory() {

        return ResponseEntity.ok(postCategoryService.getCategory());
    }
}
