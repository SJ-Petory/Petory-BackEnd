package com.sj.Petory.domain.pet.controller;

import com.sj.Petory.domain.member.dto.MemberAdapter;
import com.sj.Petory.domain.pet.dto.*;
import com.sj.Petory.domain.pet.entity.Pet;
import com.sj.Petory.domain.pet.service.PetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pets")
@RequiredArgsConstructor
public class PetController {
    private final PetService petService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Boolean> registerPet(
            @AuthenticationPrincipal MemberAdapter memberAdapter
            , @ModelAttribute @Valid PetRegister.Request request) {

        return ResponseEntity.ok(
                petService.registerPet(memberAdapter, request));
    }

    @PatchMapping(value = "/{petId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Boolean> petUpdate(
            @AuthenticationPrincipal MemberAdapter memberAdapter
            , @PathVariable("petId") long petId
            , @ModelAttribute UpdatePetRequest request) {

        return ResponseEntity.ok(
                petService.petUpdate(memberAdapter, petId, request));
    }

    @DeleteMapping("/{petId}")
    public ResponseEntity<Boolean> petDelete(
            @AuthenticationPrincipal MemberAdapter memberAdapter
            , @PathVariable("petId") long petId) {

        return ResponseEntity.ok(
                petService.petDelete(memberAdapter, petId));
    }



    @GetMapping("/caregiver")
    public ResponseEntity<Page<ICarePetListResponse>> getPetsICareFor(
            @AuthenticationPrincipal MemberAdapter memberAdapter
            , Pageable pageable) {

        return ResponseEntity.ok(
                petService.getPetsICareFor(memberAdapter, pageable));
    }

    @GetMapping("/species")
    public ResponseEntity<Page<SpeciesListResponse>> getSpeciesList(Pageable pageable) {

        return ResponseEntity.ok(petService.getSpeciesList(pageable));
    }

    @GetMapping("/breed/{speciesId}")
    public ResponseEntity<Page<BreedListResponse>> getBreedListForSpecies(
            @PathVariable("speciesId") Long speciesId
            , Pageable pageable) {

        return ResponseEntity.ok(petService.getBreedListForSpecies(speciesId, pageable));
    }
}
