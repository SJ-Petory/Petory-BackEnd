package com.sj.Petory.domain.post.service;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sj.Petory.common.s3.AmazonS3Service;
import com.sj.Petory.domain.member.dto.MemberAdapter;
import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.member.repository.MemberRepository;
import com.sj.Petory.domain.post.comment.Comment;
import com.sj.Petory.domain.post.comment.CommentRepository;
import com.sj.Petory.domain.post.comment.CommentStatus;
import com.sj.Petory.domain.post.dto.*;
import com.sj.Petory.domain.post.entity.Post;
import com.sj.Petory.domain.post.entity.PostCategory;
import com.sj.Petory.domain.post.entity.PostDocument;
import com.sj.Petory.domain.post.entity.PostImage;
import com.sj.Petory.domain.post.repository.PostCategoryRepository;
import com.sj.Petory.domain.post.repository.PostEsRepository;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private final PostEsRepository postEsRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ElasticsearchClient elasticsearchClient;


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

        //postDocument 저장
        postEsRepository.save(post.toDocument());

        return true;
    }

    private Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }

    public List<PostListResponse> getPostList(Pageable pageable) {

        return postRepository.findByStatus(PostStatus.ACTIVE, pageable).stream()
                .map(post -> PostListResponse.builder()
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

        Post post = getPostByPostId(postId);

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

        if (request.getNewImages().stream().anyMatch(img -> !img.isEmpty())) {
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

    @Transactional
    public boolean deletePost(
            final long postId, final MemberAdapter memberAdapter) {

        Member member = getMemberByEmail(memberAdapter.getEmail());
        Post post = getPostByPostId(postId);

        validatePostMember(post, member);

        post.setStatus(PostStatus.DELETED);

        for (Comment comment : post.getCommentList()) {
            comment.updateStatus(CommentStatus.DELETED);
        }
        return true;
    }

    private Post getPostByPostId(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.INVALID_POST));
    }

    public PostSearchResponse searchPost(
            final String keyword) throws IOException {

        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index("posts")
                .query(q -> q
                        .multiMatch(mm -> mm
                                .query(keyword)
                                .fields("title^3", "content")
                        )
                )
                .sort(so -> so
                        .field(f -> f.field("commentCount").order(SortOrder.Desc)))
                .sort(so -> so
                        .field(f -> f.field("sympathyCount").order(SortOrder.Desc)))
                .sort(so -> so
                        .field(f -> f.field("createdAt").order(SortOrder.Desc)))
                .highlight(h -> h
                        .fields("title", f -> f
                                .preTags("<em>")
                                .postTags("</em>"))
                        .fields("content", f -> f
                                .preTags("<em>")
                                .postTags("</em>"))
                )

        );

        SearchResponse<PostDocument> posts = elasticsearchClient.search(
                searchRequest, PostDocument.class);

        List<PostSearchResponse.PostWrapper> postWrappers = posts.hits().hits().stream()
                .map(hit -> {
                    PostDocument doc = hit.source();

                    //멤버 정보 세팅
                    assert doc != null;

                    PostSearchResponse.Member member =
                            PostSearchResponse.toMemberResponse(
                                    memberRepository.findById(doc.getMemberId())
                                            .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND)));

                    Post postEntity = postRepository.findById(doc.getPostId())
                            .filter(post -> PostStatus.ACTIVE.equals(post.getStatus()))
                            .orElse(null);

                    PostSearchResponse.Post post = PostSearchResponse.toPostResponse(postEntity);
                    post.setCommentTotal(commentRepository.countAllByPost(postEntity));
                    post.setSympathyTotal(sympathyRepository.countAllByPost(postEntity));

                    Map<String, List<String>> highlight = hit.highlight();

                    if (highlight != null) {
                        if (highlight.containsKey("title")) {
                            post.setTitle(highlight.get("title").get(0));
                        }
                        if (highlight.containsKey("content")) {
                            post.setContent(highlight.get("content").get(0));
                        }
                    }
                    return PostSearchResponse.PostWrapper.builder()
                            .member(member)
                            .post(post)
                            .build();
                }).toList();

        return PostSearchResponse.builder()
                .posts(postWrappers).build();
    }
}
