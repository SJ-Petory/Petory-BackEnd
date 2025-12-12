package com.sj.Petory.domain.member.service;

import com.sj.Petory.domain.member.dto.SignIn;
import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.member.repository.MemberRepository;
import com.sj.Petory.domain.member.type.MemberStatus;
import com.sj.Petory.domain.member.type.Role;
import com.sj.Petory.domain.pet.entity.Pet;
import com.sj.Petory.domain.pet.type.PetGender;
import com.sj.Petory.domain.schedule.dto.CreateScheduleRequest;
import com.sj.Petory.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GuestService {

    private final JwtUtils jwtUtils;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public SignIn.Response signInGuest() {
        String uuid = UUID.randomUUID().toString();
        String guestName = "게스트_" + uuid.substring(0, 5);
        String guestEmail = uuid.substring(0, 8) + "@guest.petory";

        Member guest = Member.builder()
                .email(guestEmail)
                .name(guestName)
                .password(passwordEncoder.encode("guest_pass"))
                .phone("010-1234-5678")
                .image("IMAGE DEFAULT URL")
                .status(MemberStatus.ACTIVE)
                .role(Role.GUEST).build();

        memberRepository.save(guest);
        
        return SignIn.Response.toResponse(
                jwtUtils.generateToken(guestEmail, "ATK", guest.getRole().getKey())
                , jwtUtils.generateToken(guestEmail, "RTK", guest.getRole().getKey()));
    }
}