package com.sj.Petory.domain.post.service;

import com.sj.Petory.domain.member.dto.MemberAdapter;
import com.sj.Petory.domain.post.dto.CreatePostRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PostService {
    public Boolean createPost(MemberAdapter memberAdapter, CreatePostRequest createPostRequest) {

        return true;
    }
}
