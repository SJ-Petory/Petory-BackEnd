package com.sj.Petory.domain.pet.controller;

import com.sj.Petory.domain.member.dto.MemberAdapter;
import com.sj.Petory.domain.pet.dto.PetRegister;
import com.sj.Petory.domain.pet.service.PetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pets")
@RequiredArgsConstructor
public class PetController {
    private final PetService petService;

    @PostMapping
    public ResponseEntity<Boolean> registerPet(
            @AuthenticationPrincipal MemberAdapter memberAdapter
            , @RequestBody @Valid PetRegister.Request request) {

        return ResponseEntity.ok(
                petService.registerPet(memberAdapter, request));
    }

}
