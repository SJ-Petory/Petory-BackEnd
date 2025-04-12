package com.sj.Petory.domain.notification.entity;

import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.notification.type.NoticeType;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "schedulenotification")
@EntityListeners(AuditingEntityListener.class)
public class ScheduleNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_notification_id")
    private Long scheduleNotificationId;

    @Column(name = "receive_member")
    @OneToMany(mappedBy = "scheduleNotification")
    private List<ScheduleNotificationReceiver> receiveMemberList;

    @Column(name = "receive_member_id")
    private Long receiveMemberId;

    @Column(name = "notice_type")
    @Enumerated(EnumType.STRING)
    private NoticeType noticeType;

    @Column(name = "entity_id")
    private Long entityId;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
