package com.sj.Petory.domain.pet.service;

import com.sj.Petory.common.s3.AmazonS3Service;
import com.sj.Petory.domain.caregiver.repository.CareGiverRepository;
import com.sj.Petory.domain.member.dto.MemberAdapter;
import com.sj.Petory.domain.member.dto.PetResponse;
import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.member.repository.MemberRepository;
import com.sj.Petory.domain.member.type.Role;
import com.sj.Petory.domain.pet.dto.*;
import com.sj.Petory.domain.pet.entity.Breed;
import com.sj.Petory.domain.pet.entity.Pet;
import com.sj.Petory.domain.pet.entity.Species;
import com.sj.Petory.domain.pet.repository.BreedRepository;
import com.sj.Petory.domain.pet.repository.PetRepository;
import com.sj.Petory.domain.pet.repository.SpeciesRepository;
import com.sj.Petory.domain.pet.type.PetStatus;
import com.sj.Petory.exception.AdminException;
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
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PetService {

    private final MemberRepository memberRepository;
    private final PetRepository petRepository;
    private final SpeciesRepository speciesRepository;
    private final BreedRepository breedRepository;
    private final CareGiverRepository careGiverRepository;
    private final AmazonS3Service amazonS3Service;

    @Transactional
    public boolean registerPet(
            final MemberAdapter memberAdapter,
            final PetRegister.Request request) {
        Member member = getMemberByEmail(memberAdapter.getEmail());

        Species species = speciesRepository.findBySpeciesId(
                        request.getSpeciesId())
                .orElseThrow(() -> new PetException(ErrorCode.SPECIES_NOT_FOUND));

        Breed breed = breedRepository.findByBreedId(request.getBreedId())
                .orElseThrow(() -> new PetException(ErrorCode.BREED_NOT_FOUND));

        String imageUrl = "";
        if (Objects.nonNull(request.getImage()) && !request.getImage().isEmpty()) {
            imageUrl = amazonS3Service.upload(request.getImage());
        }
        petRepository.save(request.toEntity(member, species, breed, imageUrl));

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

        String newImage = amazonS3Service.updateImage(
                pet.getPetImage(), request.getImage());

        pet.updateInfo(request, newImage);

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


    public Page<PetResponse> getRegisterPetList(
            final MemberAdapter memberAdapter, final Long memberId, final Pageable pageable) {

        getMemberByEmail(memberAdapter.getEmail());

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        List<PetResponse> petResponseList = petRepository.findByMember(member)
                .stream().map(pet -> pet.toDto(
                        breedRepository.findByBreedId(pet.getBreed())
                                .orElseThrow(() -> new MemberException(ErrorCode.BREED_NOT_FOUND))
                )).toList();

        return new PageImpl<>(petResponseList, pageable, petResponseList.size());
    }

    @Transactional
    public void registerSpecies(
            final MemberAdapter memberAdapter, final CreateSpeciesRequest request) {

        checkAdminById(memberAdapter.getMemberId());

        if (speciesRepository.existsBySpeciesName(request.getName())) {
            throw new PetException(ErrorCode.SPECIES_DUPLICATED);
        }

        speciesRepository.save(Species.builder()
                .speciesName(request.getName()).build());

    }

    private void checkAdminById(final long id) {

        memberRepository.findByMemberIdAndRole(id, Role.ADMIN)
                .orElseThrow(() -> new AdminException(ErrorCode.NOT_ADMIN_USER));
    }

    @Transactional
    public void deleteSpecies(final MemberAdapter memberAdapter, final Long speciesId) {

        checkAdminById(memberAdapter.getMemberId());

        Species species = speciesRepository.findById(speciesId)
                .orElseThrow(() -> new PetException(ErrorCode.SPECIES_NOT_FOUND));

        if (petRepository.existsBySpecies(species)) {
            throw new PetException(ErrorCode.SPECIES_IN_USE);
        }

        speciesRepository.delete(species);
    }
}
