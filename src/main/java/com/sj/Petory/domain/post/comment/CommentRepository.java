package com.sj.Petory.domain.post.comment;

import com.sj.Petory.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    long countAllByPostAndStatus(Post post, CommentStatus commentStatus);

    Optional<Comment> findByCommentIdAndStatus(long commentId, CommentStatus commentStatus);
}
