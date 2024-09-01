package com.sj.Petory.domain.friend.controller;

import com.sj.Petory.domain.friend.dto.FriendListResponse;
import com.sj.Petory.domain.friend.dto.MemberSearchResponse;
import com.sj.Petory.domain.friend.service.FriendService;
import com.sj.Petory.domain.member.dto.MemberAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

//    @GetMapping("/keyword")
//    public ResponseEntity<Page<MemberDocument>> searchMember(
//            @RequestParam("keyword") String keyword
//            , Pageable pageable) {
//
//        return ResponseEntity.ok(
//                friendService.searchMember(keyword, pageable));
//    }

    @GetMapping("/search")
    public ResponseEntity<Page<MemberSearchResponse>> searchMember(
            @AuthenticationPrincipal MemberAdapter memberAdapter,
            @RequestParam("keyword") String keyword
            , Pageable pageable) {

        return ResponseEntity.ok(
                friendService.searchMember(
                        memberAdapter, keyword, pageable)
        );
    }

    @PostMapping("/{memberId}")
    public ResponseEntity<Boolean> friendRequest(
            @AuthenticationPrincipal MemberAdapter memberAdapter
            , @PathVariable("memberId") Long memberId) {

        return ResponseEntity.ok(
                friendService.friendRequest(
                        memberAdapter, memberId));
    }

    @GetMapping
    public ResponseEntity<Page<FriendListResponse>> friendList(
            @AuthenticationPrincipal MemberAdapter memberAdapter
            , @RequestParam("status") String status
            , Pageable pageable) {

        return ResponseEntity.ok(
                friendService.friendList(
                        memberAdapter, status, pageable));
    }

    @PatchMapping("/{memberId}")
    public ResponseEntity<Boolean> requestProcess(
            @AuthenticationPrincipal MemberAdapter memberAdapter
            , @PathVariable("memberId") Long memberId
            , @RequestParam("status") String status) {

        return ResponseEntity.ok(
                friendService.requestProcess(
                        memberAdapter, memberId, status));
    }
}
