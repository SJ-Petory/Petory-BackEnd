package com.sj.Petory.domain.pet.service;

import com.sj.Petory.domain.friend.repository.FriendRepository;
import com.sj.Petory.domain.friend.repository.FriendStatusRepository;
import com.sj.Petory.domain.member.dto.MemberAdapter;
import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.member.repository.MemberRepository;
import com.sj.Petory.domain.pet.dto.*;
import com.sj.Petory.domain.pet.entity.Breed;
import com.sj.Petory.domain.pet.entity.Pet;
import com.sj.Petory.domain.pet.entity.Species;
import com.sj.Petory.domain.pet.repository.BreedRepository;
import com.sj.Petory.domain.caregiver.repository.CareGiverRepository;
import com.sj.Petory.domain.pet.repository.PetRepository;
import com.sj.Petory.domain.pet.repository.SpeciesRepository;
import com.sj.Petory.domain.pet.type.PetStatus;
import com.sj.Petory.exception.MemberException;
import com.sj.Petory.exception.PetException;
import com.sj.Petory.exception.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        Member member = getMemberByEmail(memberAdapter.getEmail());

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

    @Transactional
    public boolean petUpdate(
            final MemberAdapter memberAdapter
            , final long petId
            , final UpdatePetRequest request) {

        Member member = getMemberByEmail(memberAdapter.getEmail());

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

        Member member = getMemberByEmail(memberAdapter.getEmail());
        Pet pet = getPetById(petId);

        validatePetMember(petId, member);

        pet.updateStatus(PetStatus.DELETED);

        return true;
    }

    private void validatePetMember(long petId, Member member) {
        petRepository.findByPetIdAndMember(petId, member)
                .orElseThrow(() -> new PetException(ErrorCode.PET_MEMBER_UNMATCHED));
    }

    public Page<ICarePetListResponse> getPetsICareFor(
            final MemberAdapter memberAdapter
            , final Pageable pageable) {

        Member member = getMemberByEmail(memberAdapter.getEmail());

        return careGiverRepository.findByMember(member, pageable)
                .map(careGiver -> careGiver.toDto(
                        breedRepository.findByBreedId(careGiver.getPet().getBreed())
                                .orElseThrow(() -> new PetException(ErrorCode.BREED_NOT_FOUND))
                ));
    }

    public Page<SpeciesListResponse> getSpeciesList(final Pageable pageable) {

        List<SpeciesListResponse> speciesList =
                speciesRepository.findAll().stream()
                        .map(Species::toListDto).toList();

        return new PageImpl<>(speciesList, pageable, speciesList.size());
    }

    public Page<BreedListResponse> getBreedListForSpecies(
            final Long speciesId, final Pageable pageable) {

        List<BreedListResponse> breedList = breedRepository.findBySpecies(
                        speciesRepository.findById(speciesId)
                                .orElseThrow(() -> new PetException(ErrorCode.SPECIES_NOT_FOUND)))
                .stream().map(Breed::toListDto).toList();

        return new PageImpl<>(breedList, pageable, breedList.size());
    }


}
