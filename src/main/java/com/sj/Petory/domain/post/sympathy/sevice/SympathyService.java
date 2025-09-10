package com.sj.Petory.domain.post.sympathy.sevice;

import com.sj.Petory.domain.member.dto.MemberAdapter;
import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.member.repository.MemberRepository;
import com.sj.Petory.domain.post.entity.Post;
import com.sj.Petory.domain.post.repository.PostRepository;
import com.sj.Petory.domain.post.sympathy.dto.SympathyRegister;
import com.sj.Petory.domain.post.sympathy.entity.Sympathy;
import com.sj.Petory.domain.post.sympathy.type.SympathyType;
import com.sj.Petory.domain.post.sympathy.repository.SympathyRepository;
import com.sj.Petory.domain.post.type.PostStatus;
import com.sj.Petory.exception.MemberException;
import com.sj.Petory.exception.PostException;
import com.sj.Petory.exception.SympathyException;
import com.sj.Petory.exception.type.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SympathyService {

    private final MemberRepository memberRepository;
    private final SympathyRepository sympathyRepository;
    private final PostRepository postRepository;

    @Transactional
    public Boolean sympathyRegister(
            final MemberAdapter memberAdapter,
            final Long postId, final SympathyRegister request) {

        Member member = getMemberByEmail(memberAdapter.getEmail());
        Post post = getPostById(postId);

        SympathyType type;

        try {
            type = SympathyType.valueOf(request.getType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new SympathyException(ErrorCode.INVALID_SYMPATHY_TYPE);
        }

        sympathyRepository.findByPostAndMember(post, member)
                .ifPresentOrElse(sympathy -> sympathy.setType(type),
                        () -> {
                            Sympathy newSympathy =
                                    Sympathy.builder()
                                            .member(member)
                                            .post(post)
                                            .type(type)
                                            .build();
                            sympathyRepository.save(newSympathy);
                        });

        return true;
    }

    private Post getPostById(Long postId) {

        return postRepository.findByPostIdAndStatus(postId, PostStatus.ACTIVE)
                .orElseThrow(() -> new PostException(ErrorCode.INVALID_POST));
    }

    private Member getMemberByEmail(String email) {

        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }

    @Transactional
    public Boolean deleteSympathy(MemberAdapter memberAdapter, Long postId) {
        Member member = getMemberByEmail(memberAdapter.getEmail());
        Post post = getPostById(postId);
        
        sympathyRepository.deleteByPostAndMember(post, member);

        return true;
    }
}
