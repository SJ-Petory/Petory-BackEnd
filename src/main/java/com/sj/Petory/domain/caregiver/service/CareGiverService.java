package com.sj.Petory.domain.caregiver.service;

import com.sj.Petory.domain.caregiver.dto.CareGiverResponse;
import com.sj.Petory.domain.friend.entity.FriendStatus;
import com.sj.Petory.domain.friend.repository.FriendRepository;
import com.sj.Petory.domain.friend.repository.FriendStatusRepository;
import com.sj.Petory.domain.member.dto.MemberAdapter;
import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.member.repository.MemberRepository;
import com.sj.Petory.domain.caregiver.entity.CareGiver;
import com.sj.Petory.domain.pet.entity.Pet;
import com.sj.Petory.domain.caregiver.repository.CareGiverRepository;
import com.sj.Petory.domain.pet.repository.PetRepository;
import com.sj.Petory.exception.FriendException;
import com.sj.Petory.exception.MemberException;
import com.sj.Petory.exception.PetException;
import com.sj.Petory.exception.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CareGiverService {

    private final CareGiverRepository careGiverRepository;
    private final MemberRepository memberRepository;
    private final FriendRepository friendRepository;
    private final PetRepository petRepository;
    private final FriendStatusRepository friendStatusRepository;

    public boolean careGiverRegister(
            final MemberAdapter memberAdapter
            , final long petId
            , final long memberId) {

        Member member = getMemberByEmail(memberAdapter.getEmail());
        Member friend = getMemberById(memberId);
        Pet pet = getPetById(petId);

        validateFriend(member, friend);

        validatePetMember(petId, member);

        validateGareGiver(friend, pet);

        careGiverRepository.save(CareGiver.toEntity(friend, pet));

        return true;
    }

    private void validateFriend(Member member, Member friend) {
        if (Objects.equals(member.getMemberId(), friend.getMemberId())) {
            throw new PetException(ErrorCode.REQUEST_MYSELF_NOT_ALLOWED);
        }
        friendRepository.findBySendMemberAndReceiveMemberAndFriendStatus(
                        member, friend, getFriendStatus("ACCEPTED"))
                .orElseThrow(() -> new PetException(ErrorCode.FRIEND_INFO_NOT_FOUND));
    }

    private void validateGareGiver(Member friend, Pet pet) {
        careGiverRepository.findByPetAndMember(pet, friend)
                .ifPresent(info ->
                {
                    throw new PetException(ErrorCode.ALREADY_REGISTERED_MEMBER);
                });
    }

    private void validatePetMember(long petId, Member member) {
        petRepository.findByPetIdAndMember(petId, member)
                .orElseThrow(() -> new PetException(ErrorCode.PET_MEMBER_UNMATCHED));
    }

    private FriendStatus getFriendStatus(String status) {
        return friendStatusRepository.findByStatus(status)
                .orElseThrow(() -> new FriendException(ErrorCode.STATUS_NOT_ALLOWED));
    }

    private Pet getPetById(long petId) {
        return petRepository.findById(petId)
                .orElseThrow(() -> new PetException(ErrorCode.PET_NOT_FOUND));
    }

    @Transactional
    public boolean deleteCareGiver(
            final MemberAdapter memberAdapter,
            final Long petId, final Long memberId) {

        Member member = getMemberByEmail(memberAdapter.getEmail());

        petRepository.findByPetIdAndMember(petId, member)
                .orElseThrow(() -> new PetException(ErrorCode.PET_MEMBER_UNMATCHED));

        Member careGiver = getMemberById(memberId);
        Pet pet = getPetById(petId);

        careGiverRepository.findByPetAndMember(pet, careGiver)
                .orElseThrow(() -> new PetException(ErrorCode.UNMATCHED_PET_CAREGIVER));

        careGiverRepository.deleteByPetAndMember(pet, careGiver);

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

    public Page<CareGiverResponse> getCareGiverForPet(
            final MemberAdapter memberAdapter
            , final Long petId, final Pageable pageable) {

        Member member = getMemberByEmail(memberAdapter.getEmail());
        Pet pet = getPetById(petId);

        validatePetMember(petId, member);

        return careGiverRepository.findByPet(pet, pageable)
                .map(CareGiver::toDto);
    }
}
