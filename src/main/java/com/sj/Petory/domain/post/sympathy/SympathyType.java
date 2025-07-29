package com.sj.Petory.domain.post.sympathy;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SympathyType {
    LIKE("좋아요", "☺️"),
    AWESOME("멋져요", "😎"),
    FUNNY("웃겨요", "😂"),
    SAD("슬퍼요", "😢"),
    USEFUL("유용해요", "📌");

    private final String displayName;
    private final String emoji;
}
