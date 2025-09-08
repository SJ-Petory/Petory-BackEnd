package com.sj.Petory.domain.post.event;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;

@Getter
@RequiredArgsConstructor
public class PostUpdatedEvent {

    private final Long postId;

    private final String title;
    private final String content;

    private final Long categoryId;

}
