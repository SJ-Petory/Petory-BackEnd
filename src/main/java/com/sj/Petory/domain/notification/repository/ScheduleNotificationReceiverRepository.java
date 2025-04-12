package com.sj.Petory.domain.notification.repository;

import com.sj.Petory.domain.notification.entity.ScheduleNotificationReceiver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleNotificationReceiverRepository extends JpaRepository<ScheduleNotificationReceiver, Long> {
}
