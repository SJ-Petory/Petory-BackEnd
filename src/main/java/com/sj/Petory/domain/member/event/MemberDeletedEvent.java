package com.sj.Petory.domain.member.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MemberDeletedEvent {

    private final Long memberId;
}
