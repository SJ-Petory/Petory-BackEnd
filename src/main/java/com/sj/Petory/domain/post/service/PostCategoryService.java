package com.sj.Petory.domain.post.service;

import com.sj.Petory.domain.member.dto.MemberAdapter;
import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.member.repository.MemberRepository;
import com.sj.Petory.domain.member.type.Role;
import com.sj.Petory.domain.post.dto.CreatePostCategoryRequest;
import com.sj.Petory.domain.post.dto.PostCategoryResponse;
import com.sj.Petory.domain.post.entity.PostCategory;
import com.sj.Petory.domain.post.repository.PostCategoryRepository;
import com.sj.Petory.exception.AdminException;
import com.sj.Petory.exception.PostException;
import com.sj.Petory.exception.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostCategoryService {

    private final PostCategoryRepository postCategoryRepository;
    private final MemberRepository memberRepository;

    public List<PostCategoryResponse> getCategory() {

        return postCategoryRepository.findAll().stream()
                .map(PostCategory::toDto)
                .collect(Collectors.toList());
    }

    public boolean createPostCategory(
            final MemberAdapter memberAdapter, final CreatePostCategoryRequest request) {

        getAdminById(memberAdapter.getMemberId());

        isDuplicateCategory(request.getName());

        postCategoryRepository.save(PostCategory.builder()
                .categoryName(request.getName()).build());

        return true;
    }

    private void isDuplicateCategory(String name) {

        if (postCategoryRepository.existsByCategoryName(name)) {
            throw new PostException(ErrorCode.DUPLICATED_CATEGORY_NAME);
        }
    }

    private Member getAdminById(final long id) {

        return memberRepository.findByMemberIdAndRole(id, Role.ADMIN)
                .orElseThrow(() -> new AdminException(ErrorCode.NOT_ADMIN_USER));
    }
}
