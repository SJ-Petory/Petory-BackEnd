package com.sj.Petory.domain.member.service;

import com.sj.Petory.domain.member.dto.*;
import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.member.repository.MemberRepository;
import com.sj.Petory.domain.member.type.MemberStatus;
import com.sj.Petory.domain.pet.entity.Pet;
import com.sj.Petory.domain.pet.repository.PetRepository;
import com.sj.Petory.domain.post.entity.Post;
import com.sj.Petory.domain.post.repository.PostRepository;
import com.sj.Petory.exception.MemberException;
import com.sj.Petory.exception.type.ErrorCode;
import com.sj.Petory.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PetRepository petRepository;
    private final PostRepository postRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtils jwtUtils;
    private final AmazonS3Service amazonS3Service;

    public boolean signUp(final SignUp.Request request) {

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

    public Page<PostResponse> getMembersPosts(
            final MemberAdapter memberAdapter
            , final Pageable pageable) {

        return postRepository.findByMember(
                        getMemberByEmail(memberAdapter.getEmail()), pageable)
                .map(Post::toDto);
    }

    @Transactional
    public boolean updateMember(final MemberAdapter memberAdapter, final UpdateMemberRequest request) {
        Member member = getMemberByEmail(memberAdapter.getEmail());

        checkNameDuplicate(request.getName());

        if (StringUtils.hasText(request.getPassword())) {
            request.setPassword(
                    passwordEncoder.encode(request.getPassword()));
        }
        member.updateInfo(request);

        return true;
    }

    @Transactional
    public boolean deleteMember(final MemberAdapter memberAdapter) {
        Member member = getMemberByEmail(memberAdapter.getEmail());

        validateDeleteMember(member);

        member.updateStatus(MemberStatus.DELETED);

        return true;
    }

    private void validateDeleteMember(Member member) {
        if (member.getStatus().equals(MemberStatus.DELETED)) {
            throw new MemberException(ErrorCode.ALREADY_DELETED_MEMBER);
        }
    }

    public String imageUpload(MultipartFile image) {
        String imageUrl = amazonS3Service.upload(image);
        System.out.println(imageUrl);
        return imageUrl;
    }
}
