package com.sj.Petory.domain.notification.entity;

import com.sj.Petory.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "Schedulenotificationreceiver")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ScheduleNotificationReceiver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_notification_receiver_id")
    private Long scheduleNotificationReceiverId;

    @ManyToOne
    @JoinColumn(name = "schedule_notification_id")
    private ScheduleNotification scheduleNotification;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "is_sent")
    private boolean isSent;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
