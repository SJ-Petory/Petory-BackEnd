package com.sj.Petory.domain.friend.controller;

import com.sj.Petory.domain.friend.dto.MemberSearchResponse;
import com.sj.Petory.domain.friend.service.FriendInfoService;
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
public class FriendInfoController {

    private final FriendInfoService friendService;

//    @GetMapping("/keyword")
//    public ResponseEntity<Page<MemberDocument>> searchMember(
//            @RequestParam("keyword") String keyword
//            , Pageable pageable) {
//
//        return ResponseEntity.ok(
//                friendService.searchMember(keyword, pageable));
//    }

    @GetMapping("/keyword")
    public ResponseEntity<Page<MemberSearchResponse>> searchMember(
            @AuthenticationPrincipal MemberAdapter memberAdapter,
            @RequestParam("keyword") String keyword
            , Pageable pageable) {

        return ResponseEntity.ok(
                friendService.searchMember(
                        memberAdapter, keyword, pageable)
        );
    }

    @PostMapping
    public ResponseEntity<Boolean> friendRequest(
            @AuthenticationPrincipal MemberAdapter memberAdapter
            , @RequestParam("memberId") Long memberId) {

        return ResponseEntity.ok(
                friendService.friendRequest(
                        memberAdapter, memberId));
    }
}
