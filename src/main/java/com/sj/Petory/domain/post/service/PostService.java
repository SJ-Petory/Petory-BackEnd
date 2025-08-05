package com.sj.Petory.domain.post.service;

import com.sj.Petory.common.s3.AmazonS3Service;
import com.sj.Petory.domain.member.dto.MemberAdapter;
import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.member.repository.MemberRepository;
import com.sj.Petory.domain.post.comment.CommentRepository;
import com.sj.Petory.domain.post.dto.AllPostResponse;
import com.sj.Petory.domain.post.dto.CreatePostRequest;
import com.sj.Petory.domain.post.dto.PostImageDto;
import com.sj.Petory.domain.post.dto.UpdatePostRequest;
import com.sj.Petory.domain.post.entity.Post;
import com.sj.Petory.domain.post.entity.PostCategory;
import com.sj.Petory.domain.post.entity.PostImage;
import com.sj.Petory.domain.post.repository.PostCategoryRepository;
import com.sj.Petory.domain.post.repository.PostImageRepository;
import com.sj.Petory.domain.post.repository.PostRepository;
import com.sj.Petory.domain.post.sympathy.SympathyRepository;
import com.sj.Petory.domain.post.type.PostStatus;
import com.sj.Petory.exception.MemberException;
import com.sj.Petory.exception.PostException;
import com.sj.Petory.exception.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
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
    private final PostImageRepository postImageRepository;
    private final AmazonS3Service s3Service;
    private final CommentRepository commentRepository;
    private final SympathyRepository sympathyRepository;

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
                    postImage.setPost(post);
                    return postImage;
                }).collect(Collectors.toList()));

        return true;
    }

    private Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }

    public List<AllPostResponse> getPostList() {

        return postRepository.findByStatus(PostStatus.ACTIVE).stream()
                .map(post -> AllPostResponse.builder()
                        .member(post.getMember().toPostMemberDto())
                        .post(post.toDto())
                        .postImageDtoList(
                                post.getPostImageList().stream()
                                        .map(PostImage::toDto)
                                        .toList())
                        .commentTotal(commentRepository.countAllByPost(post))
                        .sympathyTotal(sympathyRepository.countAllByPost(post))
                        .build()
                ).collect(Collectors.toList());
    }

    @Transactional
    public Boolean updatePost(
            final UpdatePostRequest request
            , final long postId, final MemberAdapter memberAdapter) {
        Member member = getMemberByEmail(memberAdapter.getEmail());

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.INVALID_POST));

        validatePostMember(post, member);

        post.update(request);

        if (ObjectUtils.isNotEmpty(request.getCategoryId())) {
            post.setPostCategory(
                    postCategoryRepository.findById(
                            request.getCategoryId()).orElseThrow(
                            () -> new PostException(ErrorCode.INVALID_POST_CATEGORY)));
        }

        if (!request.getDeleteImageIds().isEmpty()) {
            postImageRepository.findAllById(request.getDeleteImageIds()).stream()
                    .filter(img -> post.getPostImageList().contains(img))
                    .forEach(img -> {
                        post.getPostImageList().remove(img);
                        s3Service.delete(img.getImageUrl());
                    });
        }

        if (request.getNewImages().stream().anyMatch(img -> !img.isEmpty())){
            request.getNewImages().stream()
                    .filter(img -> !img.isEmpty())
                    .forEach(
                            img -> {
                                String uploadImg = s3Service.upload(img);
                                PostImage postImg = new PostImageDto(uploadImg).toEntity();
                                postImg.setPost(post);
                                post.addPostImage(postImg);
                            });
        }

        return true;
    }

    private void validatePostMember(Post post, Member member) {

        if (!PostStatus.ACTIVE.equals(post.getStatus())) {
            throw new PostException(ErrorCode.INVALID_POST);
        }
        if (!postRepository.existsByPostIdAndMember(
                post.getPostId(), member)) {

            throw new PostException(ErrorCode.UNMATCHED_POST_MEMBER);
        }
    }
}
