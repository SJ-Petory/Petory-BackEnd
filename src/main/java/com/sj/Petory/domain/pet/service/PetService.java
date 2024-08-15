package com.sj.Petory.domain.pet.service;

import com.sj.Petory.domain.member.dto.MemberAdapter;
import com.sj.Petory.domain.member.dto.MemberInfoResponse;
import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.member.repository.MemberRepository;
import com.sj.Petory.domain.pet.dto.PetRegister;
import com.sj.Petory.domain.pet.entity.Pet;
import com.sj.Petory.domain.pet.repository.PetRepository;
import com.sj.Petory.exception.MemberException;
import com.sj.Petory.exception.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PetService {

    private final MemberRepository memberRepository;
    private final PetRepository petRepository;
    public boolean registerPet(
            final MemberAdapter memberAdapter,
            final PetRegister.Request request) {
        Member member = getMembers(memberAdapter);

        petRepository.save(request.toEntity(member));

        return true;
    }

    private Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }
    public Member getMembers(final MemberAdapter memberAdapter) {

        return getMemberByEmail(memberAdapter.getUsername());
    }
}
