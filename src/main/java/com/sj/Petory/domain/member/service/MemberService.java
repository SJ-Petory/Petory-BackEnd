package com.sj.Petory.domain.member.service;

import com.sj.Petory.domain.member.dto.SignUp;
import com.sj.Petory.domain.member.repository.MemberRepository;
import com.sj.Petory.exception.MemberException;
import com.sj.Petory.exception.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public boolean signUp(SignUp.Request request) {

        checkEmailDuplicate(request.getEmail());
        checkNameDuplicate(request.getName());

        memberRepository.save(request.toEntity());

        return true;
    }

    public boolean checkEmailDuplicate(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new MemberException(ErrorCode.EMAIL_DUPLICATED);
        }
        return true;
    }

    public boolean checkNameDuplicate(String name) {
        if (memberRepository.existsByName(name)) {
            throw new MemberException(ErrorCode.NAME_DUPLICATED);
        }
        return true;
    }
}
