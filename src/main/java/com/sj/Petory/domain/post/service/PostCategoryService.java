package com.sj.Petory.domain.post.service;

import com.sj.Petory.domain.post.dto.PostCategoryResponse;
import com.sj.Petory.domain.post.entity.PostCategory;
import com.sj.Petory.domain.post.repository.PostCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostCategoryService {

    private final PostCategoryRepository postCategoryRepository;


    public List<PostCategoryResponse> getCategory() {

        return postCategoryRepository.findAll().stream()
                .map(PostCategory::toDto)
                .collect(Collectors.toList());
    }
}
