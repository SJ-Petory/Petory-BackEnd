package com.sj.Petory.OAuth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CachedKakaoInfo {
    private String sub;
    private String name;
    private String image;
}
