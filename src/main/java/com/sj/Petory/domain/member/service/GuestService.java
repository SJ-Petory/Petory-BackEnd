package com.sj.Petory.domain.member.service;

import com.sj.Petory.domain.member.dto.SignIn;
import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.member.repository.MemberRepository;
import com.sj.Petory.domain.member.type.MemberStatus;
import com.sj.Petory.domain.member.type.Role;
import com.sj.Petory.domain.pet.entity.Pet;
import com.sj.Petory.domain.pet.type.PetGender;
import com.sj.Petory.domain.schedule.dto.CreateScheduleRequest;
import com.sj.Petory.exception.MemberException;
import com.sj.Petory.exception.type.ErrorCode;
import com.sj.Petory.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GuestService {

    private final JwtUtils jwtUtils;
    private final MemberRepository memberRepository;

    public SignIn.Response signInGuest() {
        Member guest = memberRepository.findByEmail("guest@petory.com")
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        return SignIn.Response.toResponse(
                jwtUtils.generateToken(guest.getEmail(), "ATK", guest.getRole().getKey())
                , jwtUtils.generateToken(guest.getEmail(), "RTK", guest.getRole().getKey()));
    }
}