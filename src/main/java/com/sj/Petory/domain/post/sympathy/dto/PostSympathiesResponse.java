package com.sj.Petory.domain.post.sympathy.dto;

import com.sj.Petory.domain.post.sympathy.type.SympathyType;
import lombok.*;

@Builder
public record PostSympathiesResponse(
        long memberId,
        String memberName,
        SympathyType sympathyType
) {}
