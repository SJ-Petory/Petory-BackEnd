package com.sj.Petory.domain.member.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MemberUpdatedEvent {

    private final Long memberId;
    private final String name;
}
