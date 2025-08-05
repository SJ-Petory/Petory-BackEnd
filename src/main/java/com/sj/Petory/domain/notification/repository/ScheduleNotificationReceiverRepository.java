package com.sj.Petory.domain.notification.repository;

import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.notification.entity.ScheduleNotification;
import com.sj.Petory.domain.notification.entity.ScheduleNotificationReceiver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleNotificationReceiverRepository extends JpaRepository<ScheduleNotificationReceiver, Long> {

    @Query(value = """
            select snr.member_id
            from schedulenotificationreceiver snr
            where  snr.schedule_notification_id = :scheduleNoticeId
            """, nativeQuery = true)
    List<Long> findByScheduleNotification(
            @Param("scheduleNoticeId") Long scheduleNoticeId);
}
