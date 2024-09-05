package com.sj.Petory.domain.pet.service;

import com.sj.Petory.domain.friend.entity.FriendStatus;
import com.sj.Petory.domain.friend.repository.FriendRepository;
import com.sj.Petory.domain.friend.repository.FriendStatusRepository;
import com.sj.Petory.domain.member.dto.MemberAdapter;
import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.member.repository.MemberRepository;
import com.sj.Petory.domain.pet.dto.CareGiverPetResponse;
import com.sj.Petory.domain.pet.dto.PetRegister;
import com.sj.Petory.domain.pet.dto.UpdatePetRequest;
import com.sj.Petory.domain.pet.entity.Breed;
import com.sj.Petory.domain.pet.entity.CareGiver;
import com.sj.Petory.domain.pet.entity.Pet;
import com.sj.Petory.domain.pet.entity.Species;
import com.sj.Petory.domain.pet.repository.BreedRepository;
import com.sj.Petory.domain.pet.repository.CareGiverRepository;
import com.sj.Petory.domain.pet.repository.PetRepository;
import com.sj.Petory.domain.pet.repository.SpeciesRepository;
import com.sj.Petory.domain.pet.type.PetStatus;
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
public class PetService {

    private final MemberRepository memberRepository;
    private final PetRepository petRepository;
    private final SpeciesRepository speciesRepository;
    private final BreedRepository breedRepository;
    private final FriendRepository friendRepository;
    private final FriendStatusRepository friendStatusRepository;
    private final CareGiverRepository careGiverRepository;

    public boolean registerPet(
            final MemberAdapter memberAdapter,
            final PetRegister.Request request) {
        Member member = getMember(memberAdapter);

        Species species = speciesRepository.findBySpeciesId(
                        request.getSpeciesId())
                .orElseThrow(() -> new PetException(ErrorCode.SPECIES_NOT_FOUND));

        Breed breed = breedRepository.findByBreedId(request.getBreedId())
                .orElseThrow(() -> new PetException(ErrorCode.BREED_NOT_FOUND));

        petRepository.save(request.toEntity(member, species, breed));

        return true;
    }

    private Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }

    public Member getMember(final MemberAdapter memberAdapter) {

        return getMemberByEmail(memberAdapter.getUsername());
    }

    @Transactional
    public boolean petUpdate(
            final MemberAdapter memberAdapter
            , final long petId
            , final UpdatePetRequest request) {

        Member member = getMember(memberAdapter);

        validatePetMember(petId, member);

        Pet pet = getPetById(petId);

        pet.updateInfo(request);

        return true;
    }

    private Pet getPetById(long petId) {
        return petRepository.findById(petId)
                .orElseThrow(() -> new PetException(ErrorCode.PET_NOT_FOUND));
    }

    @Transactional
    public boolean petDelete(
            final MemberAdapter memberAdapter, final long petId) {

        Member member = getMember(memberAdapter);
        Pet pet = getPetById(petId);

        validatePetMember(petId, member);

        pet.updateStatus(PetStatus.DELETED);

        return true;
    }

    private void validatePetMember(long petId, Member member) {
        petRepository.findByPetIdAndMember(petId, member)
                .orElseThrow(() -> new PetException(ErrorCode.PET_MEMBER_UNMATCHED));
    }

    public boolean careGiverRegister(
            final MemberAdapter memberAdapter
            , final long petId
            , final long memberId) {

        Member member = getMember(memberAdapter);
        Member friend = getMemberById(memberId);
        Pet pet = getPetById(petId);

        validateFriend(member, friend);

        validatePetMember(petId, member);

        validateGareGiver(friend, pet);

        careGiverRepository.save(CareGiver.toEntity(friend, pet));

        return true;
    }

    private void validateGareGiver(Member friend, Pet pet) {
        careGiverRepository.findByPetAndMember(pet, friend)
                .ifPresent(info ->
                {
                    throw new PetException(ErrorCode.ALREADY_REGISTERED_MEMBER);
                });
    }

    private void validateFriend(Member member, Member friend) {
        if (Objects.equals(member.getMemberId(), friend.getMemberId())) {
            throw new PetException(ErrorCode.REQUEST_MYSELF_NOT_ALLOWED);
        }
        friendRepository.findBySendMemberAndReceiveMemberAndFriendStatus(
                        member, friend, getFriendStatus("ACCEPTED"))
                .orElseThrow(() -> new PetException(ErrorCode.FRIEND_INFO_NOT_FOUND));
    }

    private Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private FriendStatus getFriendStatus(String status) {
        return friendStatusRepository.findByStatus(status)
                .orElseThrow(() -> new FriendException(ErrorCode.STATUS_NOT_ALLOWED));
    }

    public Page<CareGiverPetResponse> caregiverPetList(
            final MemberAdapter memberAdapter
            , final Pageable pageable) {

        Member member = getMember(memberAdapter);

        return careGiverRepository.findByMember(member, pageable)
                .map(careGiver -> careGiver.toDto(
                        breedRepository.findByBreedId(careGiver.getPet().getBreed())
                                .orElseThrow(() -> new PetException(ErrorCode.BREED_NOT_FOUND))
                ));
    }
}
