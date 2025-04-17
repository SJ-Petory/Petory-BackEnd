package com.sj.Petory.domain.member.event;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberDeletedEvent {

    private final Long memberId;
}
