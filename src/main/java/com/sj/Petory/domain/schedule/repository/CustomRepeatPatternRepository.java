package com.sj.Petory.domain.schedule.repository;

import com.sj.Petory.domain.schedule.entity.CustomRepeatPattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomRepeatPatternRepository extends JpaRepository<CustomRepeatPattern, Long> {
}
