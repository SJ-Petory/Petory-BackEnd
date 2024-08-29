package com.sj.Petory.domain.friend.controller;

import com.sj.Petory.domain.friend.dto.MemberSearchResponse;
import com.sj.Petory.domain.friend.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


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

    @GetMapping("/keyword")
    public ResponseEntity<Page<MemberSearchResponse>> searchMember(
            @RequestParam("keyword") String keyword
            , Pageable pageable) {

        return ResponseEntity.ok(
                friendService.searchMember(
                        keyword, pageable)
        );
    }
}
