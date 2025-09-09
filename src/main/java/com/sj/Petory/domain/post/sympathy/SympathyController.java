package com.sj.Petory.domain.post.sympathy;

import com.sj.Petory.domain.member.dto.MemberAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community/sympathy")
public class SympathyController {

    private final SympathyService sympathyService;

    @PostMapping("/{postId}")
    public ResponseEntity<Boolean> sympathyRegister(
            @AuthenticationPrincipal MemberAdapter memberAdapter
            , @PathVariable("postId") Long postId
            , @RequestBody SympathyRegister request) {

        return ResponseEntity.ok(
                sympathyService.sympathyRegister(
                        memberAdapter, postId, request));
    }

}
