package com.sj.Petory.domain.notification.controller;

import com.sj.Petory.domain.notification.dto.NoticeListResponse;
import com.sj.Petory.domain.notification.dto.NoticeRedirectResponse;
import com.sj.Petory.domain.notification.service.NotificationService;
import com.sj.Petory.domain.member.dto.MemberAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/subscribe")
    public SseEmitter subscribe(
            @RequestParam("token") String token) {

        return notificationService.subscribe(token);
    }

    @GetMapping
    public ResponseEntity<Page<NoticeListResponse>> noticeList(
            @AuthenticationPrincipal MemberAdapter memberAdapter
            , Pageable pageable) {

        return ResponseEntity.ok(notificationService.noticeList(
                memberAdapter, pageable));
    }

    @PatchMapping("/{noticeId}")
    public ResponseEntity<NoticeRedirectResponse> markAsRead(
            @AuthenticationPrincipal MemberAdapter memberAdapter
            , @PathVariable Long noticeId) {

        return ResponseEntity.ok(notificationService.markAsRead(
                memberAdapter, noticeId));
    }
}