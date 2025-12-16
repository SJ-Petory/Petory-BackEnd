package com.sj.Petory.domain.post.comment.controller;

import com.sj.Petory.domain.member.dto.MemberAdapter;
import com.sj.Petory.domain.post.comment.dto.CommentRegisterRequest;
import com.sj.Petory.domain.post.comment.dto.PostCommentsResponse;
import com.sj.Petory.domain.post.comment.sevice.CommentService;
import com.sj.Petory.domain.post.comment.dto.CommentUpdateRequest;
import com.sj.Petory.domain.post.sympathy.dto.PostSympathiesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Boolean> commentRegister(
            @AuthenticationPrincipal MemberAdapter memberAdapter,
            @RequestBody CommentRegisterRequest request) {

        return ResponseEntity.ok(commentService.commentRegister(
                memberAdapter, request));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<Boolean> updateComment(
            @AuthenticationPrincipal MemberAdapter memberAdapter
            , @PathVariable("commentId") long commentId
            , @RequestBody CommentUpdateRequest request) {

        return ResponseEntity.ok(
                commentService.updateComment(memberAdapter, commentId, request));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Boolean> deleteComment(
            @AuthenticationPrincipal MemberAdapter memberAdapter
            , @PathVariable("commentId") long commentId) {

        return ResponseEntity.ok(
                commentService.deleteComment(
                        memberAdapter, commentId));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<List<PostCommentsResponse>> getComments(
            @AuthenticationPrincipal MemberAdapter memberAdapter
            , @PathVariable("postId") Long postId) {

        return ResponseEntity.ok(commentService.getComments(
                memberAdapter, postId));
    }
}
