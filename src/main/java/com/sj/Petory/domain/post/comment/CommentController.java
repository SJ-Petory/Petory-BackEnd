package com.sj.Petory.domain.post.comment;

import com.sj.Petory.domain.member.dto.MemberAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Boolean> commentRegister(
            @AuthenticationPrincipal MemberAdapter memberAdapter,
            @RequestBody CommentRegisterRequest request) {

        return ResponseEntity.ok(commentService.commentRegister(
                memberAdapter, request));
    }
}
