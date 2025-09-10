package com.sj.Petory.domain.post.sympathy.repository;

import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.post.entity.Post;
import com.sj.Petory.domain.post.sympathy.entity.Sympathy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SympathyRepository extends JpaRepository<Sympathy, Long> {

    long countAllByPost(Post post);

    Optional<Sympathy> findByPostAndMember(Post post, Member member);

    void deleteByPostAndMember(Post post, Member member);
}
