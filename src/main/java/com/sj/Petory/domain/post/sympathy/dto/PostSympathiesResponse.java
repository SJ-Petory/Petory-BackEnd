package com.sj.Petory.domain.post.sympathy.dto;

import com.sj.Petory.domain.post.sympathy.type.SympathyType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostSympathiesResponse {

    private long memberId;
    private String memberName;
    private SympathyType sympathyType;
}
