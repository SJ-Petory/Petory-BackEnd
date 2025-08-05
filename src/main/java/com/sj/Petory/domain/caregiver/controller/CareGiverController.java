package com.sj.Petory.domain.caregiver.controller;

import com.sj.Petory.domain.caregiver.dto.CareGiverResponse;
import com.sj.Petory.domain.caregiver.service.CareGiverService;
import com.sj.Petory.domain.member.dto.MemberAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/caregivers")
public class CareGiverController {

    private final CareGiverService careGiverService;

    @PostMapping
    public ResponseEntity<Boolean> CareGiverRegister(
            @AuthenticationPrincipal MemberAdapter memberAdapter
            , @RequestParam("petId") long petId
            , @RequestParam("memberId") long memberId) {

        return ResponseEntity.ok(
                careGiverService.careGiverRegister(memberAdapter, petId, memberId));
    }

    @GetMapping
    public ResponseEntity<Page<CareGiverResponse>> getCareGiverForPet(
            @AuthenticationPrincipal MemberAdapter memberAdapter
            , @RequestParam("petId") Long petId
            , Pageable pageable) {

        return ResponseEntity.ok(
                careGiverService.getCareGiverForPet(
                        memberAdapter, petId, pageable));
    }

    @DeleteMapping
    public ResponseEntity<Boolean> deleteCareGiver(
            @AuthenticationPrincipal MemberAdapter memberAdapter
            , @RequestParam("petId") Long petId
            , @RequestParam("memberId") Long memberId) {

        return ResponseEntity.ok(careGiverService.deleteCareGiver(
                memberAdapter, petId, memberId));
    }
}
