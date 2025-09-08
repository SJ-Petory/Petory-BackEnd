package com.sj.Petory.domain.post.repository;

import com.sj.Petory.domain.post.entity.PostDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PostEsRepository extends ElasticsearchRepository<PostDocument, Long> {
    void deleteByMemberId(Long memberId);
}
