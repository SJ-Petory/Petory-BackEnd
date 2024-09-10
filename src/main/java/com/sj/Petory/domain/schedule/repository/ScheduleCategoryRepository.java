package com.sj.Petory.domain.schedule.repository;

import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.schedule.entity.ScheduleCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScheduleCategoryRepository extends JpaRepository<ScheduleCategory, Long> {

    Optional<ScheduleCategory> findByCategoryName(String name);

    Optional<ScheduleCategory> findByCategoryId(Long id);

    Page<ScheduleCategory> findByMember(Member member, Pageable pageable);
}
