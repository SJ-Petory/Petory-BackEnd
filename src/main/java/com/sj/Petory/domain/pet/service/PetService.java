package com.sj.Petory.domain.pet.service;

import com.sj.Petory.domain.member.dto.MemberAdapter;
import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.member.repository.MemberRepository;
import com.sj.Petory.domain.pet.dto.PetRegister;
import com.sj.Petory.domain.pet.dto.UpdatePetRequest;
import com.sj.Petory.domain.pet.entity.Breed;
import com.sj.Petory.domain.pet.entity.Pet;
import com.sj.Petory.domain.pet.entity.Species;
import com.sj.Petory.domain.pet.repository.BreedRepository;
import com.sj.Petory.domain.pet.repository.PetRepository;
import com.sj.Petory.domain.pet.repository.SpeciesRepository;
import com.sj.Petory.exception.MemberException;
import com.sj.Petory.exception.PetException;
import com.sj.Petory.exception.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PetService {

    private final MemberRepository memberRepository;
    private final PetRepository petRepository;
    private final SpeciesRepository speciesRepository;
    private final BreedRepository breedRepository;

    public boolean registerPet(
            final MemberAdapter memberAdapter,
            final PetRegister.Request request) {
        Member member = getMembers(memberAdapter);

        Species species = speciesRepository.findBySpeciesId(request.getSpeciesId());
        Breed breed = breedRepository.findByBreedId(request.getBreedId());

        petRepository.save(request.toEntity(member, species, breed));

        return true;
    }

    private Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }

    public Member getMembers(final MemberAdapter memberAdapter) {

        return getMemberByEmail(memberAdapter.getUsername());
    }

    @Transactional
    public boolean petUpdate(
            final MemberAdapter memberAdapter
            , final long petId
            , final UpdatePetRequest request) {

        Member member = getMembers(memberAdapter);

        Pet pet = getPetById(petId);

        pet.updateInfo(request);

        return true;
    }

    private Pet getPetById(long petId) {
        return petRepository.findById(petId)
                .orElseThrow(() -> new PetException(ErrorCode.PET_NOT_FOUND));
    }
}
