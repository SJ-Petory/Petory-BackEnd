package com.sj.Petory.domain.member.service;

import com.sj.Petory.domain.member.dto.SignUp;
import com.sj.Petory.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public Boolean signUp(SignUp.Request request) {
        memberRepository.save(request.toEntity());
        return true;
    }
}
