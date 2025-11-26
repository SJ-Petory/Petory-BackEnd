package com.sj.Petory.domain.post.repository;

import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.post.entity.Post;
import com.sj.Petory.domain.post.type.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Range;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findByPostIdAndStatus(Long id, PostStatus status);

    Page<Post> findByMember(Member member, Pageable pageable);

    @EntityGraph(attributePaths = {"member"})
    Page<Post> findByStatus(PostStatus postStatus, Pageable pageble);

    boolean existsByPostIdAndMember(long postId, Member member);

    List<Post> findAllByStatus(PostStatus postStatus);

    Page<Post> findByMemberAndStatus(Member member, PostStatus postStatus, Pageable pageable);
}
