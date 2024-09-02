package com.sj.Petory.domain.friend.service;

import com.sj.Petory.domain.friend.dto.FriendDetailResponse;
import com.sj.Petory.domain.friend.dto.FriendListResponse;
import com.sj.Petory.domain.friend.dto.MemberSearchResponse;
import com.sj.Petory.domain.friend.dto.PetInfo;
import com.sj.Petory.domain.friend.entity.FriendInfo;
import com.sj.Petory.domain.friend.entity.FriendStatus;
import com.sj.Petory.domain.friend.repository.FriendRepository;
import com.sj.Petory.domain.friend.repository.FriendStatusRepository;
import com.sj.Petory.domain.member.dto.MemberAdapter;
import com.sj.Petory.domain.member.dto.PetResponse;
import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.member.repository.MemberRepository;
import com.sj.Petory.domain.pet.entity.CareGiver;
import com.sj.Petory.domain.pet.entity.Pet;
import com.sj.Petory.domain.pet.repository.CareGiverRepository;
import com.sj.Petory.domain.pet.repository.PetRepository;
import com.sj.Petory.exception.FriendException;
import com.sj.Petory.exception.MemberException;
import com.sj.Petory.exception.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final FriendStatusRepository friendStatusRepository;
    private final MemberRepository memberRepository;
    private final PetRepository petRepository;
    private final CareGiverRepository careGiverRepository;
//    public Page<MemberDocument> searchMember(
//            final String keyword, final Pageable pageable) {
//
//        return memberElasticsearchRepository.findByName(keyword, pageable);
//    }

    public Page<MemberSearchResponse> searchMember(
            final MemberAdapter memberAdapter
            , final String keyword, final Pageable pageable) {

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
            friendRepository.save(
                    FriendInfo.friendRequestToEntity(member, friend));
        }

        return true;
    }

    private boolean validateFriendRequest(Member member, Member friend) {
        if (member.equals(friend)) {
            throw new FriendException(ErrorCode.REQUEST_MYSELF_NOT_ALLOWED);
        }
        friendRepository.findByMemberAndFriend(
                        member, friend)
                .ifPresent(info -> {
                    if (info.getFriendStatus().getFriendStatusId() == 1) {
                        throw new FriendException(ErrorCode.ALREADY_FRIEND_REQUEST);
                    } else if (info.getFriendStatus().getFriendStatusId() == 2) {
                        throw new FriendException(ErrorCode.ALREADY_FRIEND_MEMBER);
                    }
                });
        friendRepository.findByMemberAndFriend(
                        friend, member)
                .ifPresent(info -> {
                    if (info.getFriendStatus().getFriendStatusId() == 1) {
                        throw new FriendException(ErrorCode.ALREADY_RECEIVE_FRIEND_REQUEST);
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

        FriendStatus friendStatus = getFriendStatus(status);
        Member member = getMemberByEmail(memberAdapter.getEmail());

        switch (friendStatus.getStatus()) {
            case "PENDING" -> {
                return friendRepository.findByFriendAndFriendStatus(
                        member
                        , getFriendStatus(status)
                        , pageable)
                        .map(friendInfo -> friendInfo.toDto(member.getMemberId()));
            }
            case "ACCEPTED" -> {
                return friendRepository.findByMemberAndFriendStatusOrFriendAndFriendStatus
                                (member, friendStatus, member, friendStatus, pageable)
                        .map(friendInfo -> friendInfo.toDto(member.getMemberId()));
            }
        }

        return null;
    }

    private FriendStatus getFriendStatus(String status) {
        return friendStatusRepository.findByStatus(status)
                .orElseThrow(() -> new FriendException(ErrorCode.STATUS_NOT_ALLOWED));
    }

    @Transactional
    public boolean requestProcess(
            final MemberAdapter memberAdapter
            , final Long memberId
            , final String status) {

        Member member = getMemberByEmail(
                memberAdapter.getEmail());

        FriendStatus pending = getFriendStatus("PENDING");

        FriendStatus friendStatus = getFriendStatus(status);

        Member friend = getMemberById(memberId);

        FriendInfo friendInfo = friendRepository.findByFriendAndMemberAndFriendStatus(
                        member, friend, pending)
                .orElseThrow(() -> new FriendException(ErrorCode.REQUEST_NOT_FOUND));

        friendInfo.setFriendStatus(friendStatus);

        return true;
    }

    public FriendDetailResponse friendDetail(
            final MemberAdapter memberAdapter
            , final Long memberId
            , final Pageable pageable) {

        Member member = getMemberByEmail(memberAdapter.getEmail());

        Member friend = getMemberById(memberId);


        return FriendDetailResponse.builder()
                .id(friend.getMemberId())
                .name(friend.getName())
                .image(friend.getImage())
                .myPets(petRepository.findByMember(member, pageable)
                        .stream()
                        .filter(mypet ->
                                careGiverRepository.findByPetAndMember(mypet, friend).isPresent()
                        )  // 돌보미로 등록된 항목만 필터링
                        .map(mypet -> new PetInfo(mypet.getPetId(), mypet.getPetName(), mypet.getPetImage()))  // PetInfo로 변환
                        .collect(Collectors.toList()))
                .careGivePets(petRepository.findByMember(friend, pageable)
                        .stream()
                        .filter(mypet ->
                                careGiverRepository.findByPetAndMember(mypet, member).isPresent()
                        )  // 돌보미로 등록된 항목만 필터링
                        .map(mypet -> new PetInfo(mypet.getPetId(), mypet.getPetName(), mypet.getPetImage()))  // PetInfo로 변환
                        .collect(Collectors.toList()))
                .build();
    }
}
