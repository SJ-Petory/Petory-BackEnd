package com.sj.Petory.common.es;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberEsRepository extends ElasticsearchRepository<MemberDocument, Long> {
    Page<MemberDocument> findByNameOrEmail(String name, String email, Pageable pageable);
}
