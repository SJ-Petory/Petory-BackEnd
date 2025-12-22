package com.sj.Petory.domain.member.repository;

import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.member.type.MemberStatus;
import com.sj.Petory.domain.member.type.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(String email);

    boolean existsByName(String name);

    Optional<Member> findByEmail(String email);

    List<Member> findAllByStatus(MemberStatus memberStatus);

    Optional<Member> findByMemberIdAndRole(long id, Role role);

    Optional<Member> findByProviderId(String providerId);

    boolean existsByProviderId(String sub);
}
