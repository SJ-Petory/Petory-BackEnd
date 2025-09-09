package com.sj.Petory.domain.post.comment;

import com.sj.Petory.domain.member.dto.MemberAdapter;
import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.member.repository.MemberRepository;
import com.sj.Petory.domain.post.entity.Post;
import com.sj.Petory.domain.post.repository.PostRepository;
import com.sj.Petory.domain.post.type.PostStatus;
import com.sj.Petory.exception.CommentException;
import com.sj.Petory.exception.MemberException;
import com.sj.Petory.exception.PostException;
import com.sj.Petory.exception.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @PostMapping
    public boolean commentRegister(
            final MemberAdapter memberAdapter
            , final CommentRegisterRequest request) {

        Member member = getMemberByEmail(memberAdapter.getEmail());
        Post post = getPostById(request.getPostId());

        commentRepository.save(
                Comment.builder()
                        .post(post)
                        .member(member)
                        .content(request.getContent())
                        .status(CommentStatus.ACTIVE)
                        .build());

        return true;
    }

    private Post getPostById(long postId) {

        return postRepository.findByPostIdAndStatus(postId, PostStatus.ACTIVE)
                .orElseThrow(() -> new PostException(ErrorCode.INVALID_POST));
    }

    private Member getMemberByEmail(String email) {

        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }

    @Transactional
    public Boolean updateComment(
            final MemberAdapter memberAdapter
            , final long commentId
            , final CommentUpdateRequest request) {

        Member member = getMemberByEmail(memberAdapter.getEmail());

        Comment comment = commentRepository.findByCommentIdAndStatus(commentId, CommentStatus.ACTIVE)
                .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND));

        if (!Objects.equals(comment.getMember().getMemberId(), member.getMemberId())) {
            throw new CommentException(ErrorCode.UNMATCHED_COMMENT_MEMBER);
        }

        if (StringUtils.hasText(request.getContent()))
            comment.updateContent(request.getContent());

        return true;
    }
}
