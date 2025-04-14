package com.sj.Petory.domain.notification.repository;

import com.sj.Petory.domain.notification.entity.ScheduleNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface ScheduleNotificationRepository extends JpaRepository<ScheduleNotification, Long> {

    @Query(value = """
        SELECT sn.*
        FROM schedulenotification sn
        JOIN schedule_notification_notice_times snt
            ON sn.schedule_notification_id = snt.schedule_notification_id
        WHERE snt.notice_time = :targetTime
        """, nativeQuery = true)
    List<ScheduleNotification> findByNoticeTime(@Param("targetTime") LocalDateTime targetTime);
    // 이렇게 List 조회 불가 NativeQUery 써야함
}
