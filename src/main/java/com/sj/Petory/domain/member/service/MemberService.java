package com.sj.Petory.domain.member.service;

import com.sj.Petory.domain.member.dto.*;
import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.member.repository.MemberRepository;
import com.sj.Petory.domain.pet.entity.Pet;
import com.sj.Petory.domain.pet.repository.PetRepository;
import com.sj.Petory.exception.MemberException;
import com.sj.Petory.exception.type.ErrorCode;
import com.sj.Petory.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PetRepository petRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtils jwtUtils;

    public boolean signUp(SignUp.Request request) {

        checkEmailDuplicate(request.getEmail());
        checkNameDuplicate(request.getName());

        request.setPassword(
                passwordEncoder.encode(request.getPassword()));

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

    public SignIn.Response signIn(SignIn.Request request) {

        Member member = getMemberByEmail(request.getEmail());

        validatedPassword(request, member);

        return SignIn.Response.toResponse(
                jwtUtils.generateToken(request.getEmail(), "ATK")
                , jwtUtils.generateToken(request.getEmail(), "RTK"));

    }

    private Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private void validatedPassword(SignIn.Request request, Member member) {
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new MemberException(ErrorCode.PASSWORD_UNMATCHED);
        }
    }


    public MemberInfoResponse getMembers(final MemberAdapter memberAdapter) {
        Member member = getMemberByEmail(memberAdapter.getUsername());

        return MemberInfoResponse.fromEntity(member);
    }

    public Page<PetResponse> getMembersPets(
            final MemberAdapter memberAdapter
            , Pageable pageable) {
        Member member = getMemberByEmail(memberAdapter.getEmail());

        return petRepository.findByMember(member, pageable)
                .map(Pet::toDto);
    }
}
