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
import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.member.repository.MemberRepository;
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

        Member sendMember = getMemberByEmail(memberAdapter.getEmail());
        Member receiveMember = getMemberById(friendId);

        if (validateFriendRequest(sendMember, receiveMember)) {
            friendRepository.save(
                    FriendInfo.friendRequestToEntity(sendMember, receiveMember));
        }

        return true;
    }

    private boolean validateFriendRequest(Member member, Member friend) {
        if (member.equals(friend)) {
            throw new FriendException(ErrorCode.REQUEST_MYSELF_NOT_ALLOWED);
        }
        friendRepository.findBySendMemberAndReceiveMember(
                        member, friend)
                .ifPresent(info -> {
                    if (info.getFriendStatus().getFriendStatusId() == 1) {
                        throw new FriendException(ErrorCode.ALREADY_FRIEND_REQUEST);
                    } else if (info.getFriendStatus().getFriendStatusId() == 2) {
                        throw new FriendException(ErrorCode.ALREADY_FRIEND_MEMBER);
                    }
                });
        friendRepository.findBySendMemberAndReceiveMember(
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

        FriendStatus requestStatus = getFriendStatus(status);

        Member receiveMember = getMemberByEmail(memberAdapter.getEmail());

        return friendRepository.findByReceiveMemberAndFriendStatus(
                        receiveMember, requestStatus, pageable)
                .map(FriendInfo::toDto);
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

        Member receiveMember = getMemberByEmail(
                memberAdapter.getEmail());

        FriendStatus pending = getFriendStatus("PENDING");

        FriendStatus friendStatus = getFriendStatus(status);

        Member sendMember = getMemberById(memberId);

        FriendInfo friendInfo = friendRepository.findBySendMemberAndReceiveMemberAndFriendStatus(
                        receiveMember, sendMember, pending)
                .orElseThrow(() -> new FriendException(ErrorCode.REQUEST_NOT_FOUND));

        friendInfo.setFriendStatus(friendStatus);

        friendRepository.save(
                FriendInfo.builder()
                        .sendMember(receiveMember)
                        .friendStatus(friendStatus)
                        .receiveMember(sendMember)
                        .build());
        return true;
    }

    public FriendDetailResponse friendDetail(
            final MemberAdapter memberAdapter
            , final Long memberId
            , final Pageable pageable) {

        Member member = getMemberByEmail(memberAdapter.getEmail());

        Member friend = getMemberById(memberId);


        FriendStatus accept = getFriendStatus("ACCEPTED");

        friendRepository.findBySendMemberAndReceiveMemberAndFriendStatus(
                        member, friend, accept)
                .orElseThrow(() -> new FriendException(ErrorCode.FRIEND_INFO_NOT_FOUND));

        return FriendDetailResponse.builder()
                .id(memberId)
                .name(friend.getName())
                .image(friend.getImage())
                .myPets(petRepository.findByMember(member, pageable)
                        .stream()
                        .filter(mypet ->
                                careGiverRepository.findByPetAndMember(mypet, friend).isPresent())
                        .map(mypet -> new PetInfo(mypet.getPetId(), mypet.getPetName(), mypet.getPetImage()))
                        .collect(Collectors.toList()))
                .careGivePets(petRepository.findByMember(friend, pageable)
                        .stream()
                        .filter(friendpet ->
                                careGiverRepository.findByPetAndMember(friendpet, member).isPresent())
                        .map(friendPet -> new PetInfo(friendPet.getPetId(), friendPet.getPetName(), friendPet.getPetImage()))
                        .collect(Collectors.toList()))
                .build();
    }
}
