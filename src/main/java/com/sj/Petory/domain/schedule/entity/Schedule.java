package com.sj.Petory.domain.schedule.entity;

import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.schedule.type.PriorityType;
import com.sj.Petory.domain.schedule.type.RepeatCycle;
import com.sj.Petory.domain.schedule.type.RepeatType;
import com.sj.Petory.domain.schedule.type.ScheduleStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private ScheduleCategory scheduleCategory;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "schedule_title")
    private String scheduleTitle;

    @Column(name = "schedule_content")
    private String scheduleContent;

    @Column(name = "schedule_at")
    private LocalDateTime scheduleAt;

    @Column(name = "repeat_type")
    private RepeatType repeatType;

    @Column(name = "repeat_cycle")
    private RepeatCycle repeatCycle;

    @Column(name = "notice_yn")
    private boolean noticeYn;

    @Column(name = "notice_at")
    private long noticeAt;

    @Column(name = "priority")
    private PriorityType priority;

    @Column(name = "status")
    private ScheduleStatus status;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
