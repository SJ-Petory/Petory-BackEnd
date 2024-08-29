package com.sj.Petory.domain.friend.service;

import com.sj.Petory.domain.friend.dto.FriendListResponse;
import com.sj.Petory.domain.friend.dto.MemberSearchResponse;
import com.sj.Petory.domain.friend.entity.FriendInfo;
import com.sj.Petory.domain.friend.entity.FriendStatus;
import com.sj.Petory.domain.friend.repository.FriendInfoRepository;
import com.sj.Petory.domain.friend.repository.FriendStatusRepository;
import com.sj.Petory.domain.member.dto.MemberAdapter;
import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.member.repository.MemberRepository;
import com.sj.Petory.exception.FriendException;
import com.sj.Petory.exception.MemberException;
import com.sj.Petory.exception.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class FriendInfoService {

    private final FriendInfoRepository friendInfoRepository;
    private final FriendStatusRepository friendStatusRepository;
    private final MemberRepository memberRepository;

//    public Page<MemberDocument> searchMember(
//            final String keyword, final Pageable pageable) {
//
//        return memberElasticsearchRepository.findByName(keyword, pageable);
//    }

    public Page<MemberSearchResponse> searchMember(
            final MemberAdapter memberAdapter
            ,final String keyword, final Pageable pageable) {

        getMemberByEmail(memberAdapter.getEmail());

        return memberRepository.findByNameOrEmail(keyword, keyword, pageable)
                .map(Member::toDto);
    }

    public Boolean friendRequest(
            final MemberAdapter memberAdapter
            , final Long friendId) {

        Member member = getMemberByEmail(memberAdapter.getEmail());
        Member friend = getMemberById(friendId);

        if (validateFriendRequest(member, friend)) {
            friendInfoRepository.save(
                    FriendInfo.friendRequestToEntity(member, friend));
        }

        return true;
    }

    private boolean validateFriendRequest(Member member, Member friend) {
        if (member.equals(friend)) {
            throw new FriendException(ErrorCode.REQUEST_MYSELF_NOT_ALLOWED);
        }
        friendInfoRepository.findByMemberIdAndFriendId(
                        member, friend)
                .ifPresent(info -> {
                    if (info.getFriendStatus().getFriendStatusId() == 1) {
                        throw new FriendException(ErrorCode.ALREADY_FRIEND_REQUEST);
                    } else if (info.getFriendStatus().getFriendStatusId() == 2) {
                        throw new FriendException(ErrorCode.ALREADY_FRIEND_MEMBER);
                    }
                });

        return true;
    }

    private Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }

    public Page<FriendListResponse> friendList(
            final MemberAdapter memberAdapter,
            final String status, final Pageable pageable) {

        return friendInfoRepository.findByMemberIdAndFriendStatus(
                getMemberByEmail(memberAdapter.getEmail())
                , getFriendStatus(status)
                , pageable)
                .map(FriendInfo::toDto);
    }

    private FriendStatus getFriendStatus(String status) {
        return friendStatusRepository.findByStatus(status);
    }
}
