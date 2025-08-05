package com.sj.Petory.domain.post.repository;

import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.post.entity.Post;
import com.sj.Petory.domain.post.type.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByMember(Member member, Pageable pageable);

    @EntityGraph(attributePaths = {"member", "postImageList"})
    List<Post> findByStatus(PostStatus postStatus);


    boolean existsByPostIdAndMember(long postId, Member member);
}
