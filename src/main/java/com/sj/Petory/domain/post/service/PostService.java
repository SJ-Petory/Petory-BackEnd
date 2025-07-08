package com.sj.Petory.domain.post.service;

import com.sj.Petory.domain.member.dto.MemberAdapter;
import com.sj.Petory.domain.post.dto.CreatePostRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;

@RequiredArgsConstructor
@Service
@Slf4j
public class PostService {
    public Boolean createPost(
            final MemberAdapter memberAdapter
            , final CreatePostRequest createPostRequest) {
        log.info(createPostRequest.getImage().toString());

        return true;
    }
}
