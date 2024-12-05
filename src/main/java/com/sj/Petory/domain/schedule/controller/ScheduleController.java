package com.sj.Petory.domain.schedule.controller;

import com.sj.Petory.domain.member.dto.MemberAdapter;
import com.sj.Petory.domain.schedule.dto.*;
import com.sj.Petory.domain.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping("/category")
    public ResponseEntity<Boolean> createCategory(
            @AuthenticationPrincipal MemberAdapter memberAdapter
            , @RequestBody CreateCategoryRequest request) {

        return ResponseEntity.ok(
                scheduleService.createCategory(memberAdapter, request));
    }

    @GetMapping("/category")
    public ResponseEntity<Page<CategoryListResponse>> categoryList(
            @AuthenticationPrincipal MemberAdapter memberAdapter
            , Pageable pageable) {

        return ResponseEntity.ok(
                scheduleService.categoryList(memberAdapter, pageable));
    }

    @PostMapping
    public ResponseEntity<Boolean> createSchedule(
            @AuthenticationPrincipal MemberAdapter memberAdapter
            , @RequestBody CreateScheduleRequest request) {

        return ResponseEntity.ok(
                scheduleService.createSchedule(memberAdapter, request));
    }

    @GetMapping
    public ResponseEntity<Page<ScheduleListResponse>> scheduleList(
            @AuthenticationPrincipal MemberAdapter memberAdapter
            , Pageable pageable) {

        return ResponseEntity.ok(
                scheduleService.scheduleList(memberAdapter, pageable));
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<ScheduleDetailResponse> scheduleDetail(
            @AuthenticationPrincipal MemberAdapter memberAdapter
            , @PathVariable("scheduleId") Long scheduleId) {

        return ResponseEntity.ok(
                scheduleService.scheduleDetail(memberAdapter, scheduleId));
    }
//
//    @PatchMapping("/{scheduleId}")
//    public ResponseEntity<Boolean> scheduleUpdate(
//            @AuthenticationPrincipal MemberAdapter memberAdapter
//            , @PathVariable("scheduleId") Long scheduleId
//            , @RequestBody ScheduleUpdateRequest request) {
//
//        return ResponseEntity.ok(
//                scheduleService.scheduleUpdate(memberAdapter, scheduleId, request));
//    }
}
