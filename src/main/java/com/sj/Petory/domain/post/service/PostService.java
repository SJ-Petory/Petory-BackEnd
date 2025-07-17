package com.sj.Petory.domain.post.service;

import com.sj.Petory.common.s3.AmazonS3Service;
import com.sj.Petory.domain.member.dto.MemberAdapter;
import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.member.repository.MemberRepository;
import com.sj.Petory.domain.post.dto.CreatePostRequest;
import com.sj.Petory.domain.post.dto.PostImageDto;
import com.sj.Petory.domain.post.entity.Post;
import com.sj.Petory.domain.post.entity.PostCategory;
import com.sj.Petory.domain.post.entity.PostImage;
import com.sj.Petory.domain.post.repository.PostCategoryRepository;
import com.sj.Petory.domain.post.repository.PostRepository;
import com.sj.Petory.exception.MemberException;
import com.sj.Petory.exception.PostException;
import com.sj.Petory.exception.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class PostService {
    private final MemberRepository memberRepository;
    private final PostCategoryRepository postCategoryRepository;
    private final PostRepository postRepository;
    private final AmazonS3Service s3Service;

    @Transactional
    public Boolean createPost(
            final MemberAdapter memberAdapter
            , final CreatePostRequest createPostRequest) {

        //멤버정보 가져오기
        Member member = getMemberByEmail(memberAdapter.getEmail());
        //카테고리 정보 가져오기
        PostCategory postCategory = postCategoryRepository.findById(
                createPostRequest.getCategoryId()).orElseThrow(
                () -> new PostException(ErrorCode.INVALID_POST_CATEGORY)
        );
        //Post DB에 게시글 저장
        Post post = postRepository.save(
                createPostRequest.toEntity(member, postCategory));
        //S3에 저장하는 로직 -> img url 반환
        List<PostImageDto> postImageDtoList = new ArrayList<>();
        createPostRequest.getImage().forEach(
                img -> postImageDtoList.add(
                new PostImageDto(s3Service.upload(img))));

        //postImage 엔티티 저장 -> post에서 걍 저장해버림
        post.setPostImageList(postImageDtoList.stream().map(
                img -> {
                    PostImage postImage = img.toEntity();
                    postImage.setPostId(post);
                    return postImage;
                }).collect(Collectors.toList()));

        return true;
    }

    private Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
