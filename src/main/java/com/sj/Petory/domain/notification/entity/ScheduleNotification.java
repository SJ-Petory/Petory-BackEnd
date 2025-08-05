package com.sj.Petory.domain.notification.entity;

import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.notification.type.NoticeType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
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

    @Column(name = "entity_id")
    private Long entityId;

    @ElementCollection
    @CollectionTable(
            name = "schedule_notification_notice_times",
            joinColumns = @JoinColumn(name = "schedule_notification_id")
    )
    @Column(name = "notice_time")
    private List<LocalDateTime> noticeTimeList;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
