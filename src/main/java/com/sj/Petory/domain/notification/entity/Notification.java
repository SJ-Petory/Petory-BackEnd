package com.sj.Petory.domain.notification.entity;

import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.notification.dto.NoticeListResponse;
import com.sj.Petory.domain.notification.type.NoticeType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private NoticeType noticeType;

    @Column(name = "entity_id")
    private Long entityId;

    @Setter
    @Column(name = "is_read")
    private boolean isRead;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public NoticeListResponse toListDto() {

        return NoticeListResponse.builder()
                .noticeId(this.getNoticeId())
                .isRead(this.isRead())
                .noticeType(this.getNoticeType())
                .createdAt(this.getCreatedAt())
                .build();
    }
}
