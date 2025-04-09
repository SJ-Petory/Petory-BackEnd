package com.sj.Petory.domain.friend.service;

import com.sj.Petory.common.es.MemberEsRepository;
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
import com.sj.Petory.common.es.MemberDocument;
import com.sj.Petory.domain.member.repository.MemberRepository;
import com.sj.Petory.domain.notification.dto.NotificationPayloadDto;
import com.sj.Petory.domain.notification.service.NotificationService;
import com.sj.Petory.domain.notification.type.NoticeType;
import com.sj.Petory.domain.pet.repository.CareGiverRepository;
import com.sj.Petory.domain.pet.repository.PetRepository;
import com.sj.Petory.domain.pet.type.PetStatus;
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
    private final MemberEsRepository memberEsRepository;
    private final NotificationService notificationService;


    public Page<MemberSearchResponse> searchMember(
            final String keyword, final Pageable pageable) {

        return memberEsRepository.findByNameOrEmail(keyword, keyword, pageable)
                .map(MemberDocument::toDto);
    }

    public Boolean friendRequest(
            final MemberAdapter memberAdapter
            , final Long friendId) {

        Member sendMember = getMemberByEmail(memberAdapter.getEmail());
        Member receiveMember = getMemberById(friendId);

        validateFriendRequest(sendMember, receiveMember);

        FriendInfo friendInfo = friendRepository.save(
                FriendInfo.friendRequestToEntity(sendMember, receiveMember));

        sendNotification(receiveMember, NoticeType.FRIEND_REQUEST, friendInfo, sendMember);

        return true;
    }

    private void validateFriendRequest(Member sendMember, Member receiveMember) {
        if (sendMember.equals(receiveMember)) {
            throw new FriendException(ErrorCode.REQUEST_MYSELF_NOT_ALLOWED);
        }

        friendRepository.findBySendMemberAndReceiveMember(sendMember, receiveMember)
                .forEach(info -> {
                    if (info.getFriendStatus().getFriendStatusId() == 1) {
                        throw new FriendException(ErrorCode.ALREADY_FRIEND_REQUEST);
                    }
                    if (info.getFriendStatus().getFriendStatusId() == 2) {
                        throw new FriendException(ErrorCode.ALREADY_FRIEND_MEMBER);
                    }
                });

        friendRepository.findBySendMemberAndReceiveMember(receiveMember, sendMember)
                .forEach(info -> {
                    if (info.getFriendStatus().getFriendStatusId() == 1) {
                        throw new FriendException(ErrorCode.ALREADY_RECEIVE_FRIEND_REQUEST);
                    }
                });
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
                        sendMember, receiveMember, pending)
                .orElseThrow(() -> new FriendException(ErrorCode.REQUEST_NOT_FOUND));

        friendInfo.setFriendStatus(friendStatus);

        if (friendStatus.getStatus().equals("ACCEPTED")) {
            friendRepository.save(
                    FriendInfo.builder()
                            .sendMember(receiveMember)
                            .friendStatus(friendStatus)
                            .receiveMember(sendMember)
                            .build());
        }

        sendNotification(sendMember, NoticeType.FRIEND_PROCESS, friendInfo, receiveMember);

        return true;
    }

    private void sendNotification(Member receiveMember, NoticeType friendProcess, FriendInfo friendInfo, Member sendMember) {
        notificationService.sendNotification(
                receiveMember,
                NotificationPayloadDto.builder()
                        .receiveMemberId(receiveMember.getMemberId())
                        .noticeType(friendProcess)
                        .entityId(friendInfo.getFriendInfoId())
                        .sendMemberId(sendMember.getMemberId())
                        .sendMemberName(sendMember.getName())
                        .build()
        );
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
                .myPets(petRepository.findByMemberAndStatus(member, PetStatus.ACTIVE, pageable)
                        .stream()
                        .filter(mypet ->
                                careGiverRepository.findByPetAndMember(mypet, friend).isPresent())
                        .map(mypet -> new PetInfo(mypet.getPetId(), mypet.getPetName(), mypet.getPetImage()))
                        .collect(Collectors.toList()))
                .careGivePets(petRepository.findByMemberAndStatus(friend, PetStatus.ACTIVE, pageable)
                        .stream()
                        .filter(friendpet ->
                                careGiverRepository.findByPetAndMember(friendpet, member).isPresent())
                        .map(friendPet -> new PetInfo(friendPet.getPetId(), friendPet.getPetName(), friendPet.getPetImage()))
                        .collect(Collectors.toList()))
                .build();
    }
}
