package com.sj.Petory.domain.schedule.repository;

import com.sj.Petory.domain.schedule.entity.RepeatPattern;
import com.sj.Petory.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepeatPatternRepository extends JpaRepository<RepeatPattern, Long> {

    Optional<RepeatPattern> findBySchedule(Schedule schedule);
}
