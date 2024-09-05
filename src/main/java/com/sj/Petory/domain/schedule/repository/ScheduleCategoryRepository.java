package com.sj.Petory.domain.schedule.repository;

import com.sj.Petory.domain.schedule.entity.ScheduleCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScheduleCategoryRepository extends JpaRepository<ScheduleCategory, Long> {

    Optional<ScheduleCategory> findByCategoryName(String name);
}
