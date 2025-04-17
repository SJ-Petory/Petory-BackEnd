package com.sj.Petory.domain.member.event;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberUpdatedEvent {

    private final Long memberId;
    private final String name;
}
