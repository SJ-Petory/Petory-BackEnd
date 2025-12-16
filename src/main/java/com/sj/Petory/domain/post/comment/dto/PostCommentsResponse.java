package com.sj.Petory.domain.post.comment.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostCommentsResponse(
        long memberId,
        String memberName,
        String memberImage,
        String content,
        LocalDateTime createdAt
) {
}
