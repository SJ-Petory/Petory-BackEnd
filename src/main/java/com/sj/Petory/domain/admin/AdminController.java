package com.sj.Petory.domain.admin;

import com.sj.Petory.domain.member.dto.MemberAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/test")
    public ResponseEntity<String> test() {

        log.info("관리자 로그인");
        return null;
    }


    //게시글 카테고리 CD
    //반려동물 종 카테고리 CD
    //반려동물 세부 종 카테고리 CD
}
