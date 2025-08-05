package com.sj.Petory.domain.schedule.repository;

import com.sj.Petory.domain.schedule.entity.Schedule;
import com.sj.Petory.domain.schedule.entity.SelectDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SelectDateRepository extends JpaRepository<SelectDate, Long> {

    Optional<SelectDate> findByScheduleAndSelectedDate(Schedule schedule, LocalDateTime scheduleAt);
}
