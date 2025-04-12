package com.sj.Petory.domain.notification.repository;

import com.sj.Petory.domain.notification.entity.ScheduleNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleNotificationRepository extends JpaRepository<ScheduleNotification, Long> {
}
