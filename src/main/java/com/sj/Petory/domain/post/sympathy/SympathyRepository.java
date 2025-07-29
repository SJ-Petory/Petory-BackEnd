package com.sj.Petory.domain.post.sympathy;

import com.sj.Petory.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SympathyRepository extends JpaRepository<Sympathy, Long> {

    long countAllByPost(Post post);

}
