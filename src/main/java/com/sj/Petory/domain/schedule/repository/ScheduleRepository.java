package com.sj.Petory.domain.schedule.repository;

import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.schedule.entity.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    Page<Schedule> findByScheduleId(Schedule schedule);

    Page<Schedule> findByMember(Member member, Pageable pageable);
}
