package com.sj.Petory.domain.friend.service;

import com.sj.Petory.domain.friend.dto.MemberSearchResponse;
import com.sj.Petory.domain.friend.repository.FriendRepository;
import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final MemberRepository memberRepository;

//    public Page<MemberDocument> searchMember(
//            final String keyword, final Pageable pageable) {
//
//        return memberElasticsearchRepository.findByName(keyword, pageable);
//    }

    public Page<MemberSearchResponse> searchMember(
            final String keyword, final Pageable pageable) {

        return memberRepository.findByNameOrEmail(keyword, keyword, pageable)
                .map(Member::toDto);
    }
}
