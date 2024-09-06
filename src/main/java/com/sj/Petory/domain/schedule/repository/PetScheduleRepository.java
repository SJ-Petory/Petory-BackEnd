package com.sj.Petory.domain.schedule.repository;

import com.sj.Petory.domain.schedule.entity.PetSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetScheduleRepository extends JpaRepository<PetSchedule, Long> {
}
