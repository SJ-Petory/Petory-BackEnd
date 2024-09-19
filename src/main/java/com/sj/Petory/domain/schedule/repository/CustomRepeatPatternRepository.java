package com.sj.Petory.domain.schedule.repository;

import com.sj.Petory.domain.schedule.entity.CustomRepeatPattern;
import com.sj.Petory.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomRepeatPatternRepository extends JpaRepository<CustomRepeatPattern, Long> {

    Optional<CustomRepeatPattern> findBySchedule(Schedule schedule);
}
