package com.sj.Petory.domain.schedule.entity;

import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.schedule.dto.CustomRepeatResponse;
import com.sj.Petory.domain.schedule.dto.ScheduleDetailResponse;
import com.sj.Petory.domain.schedule.dto.ScheduleListResponse;
import com.sj.Petory.domain.schedule.type.PriorityType;
import com.sj.Petory.domain.schedule.type.RepeatCycle;
import com.sj.Petory.domain.schedule.type.RepeatType;
import com.sj.Petory.domain.schedule.type.ScheduleStatus;
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
    @Enumerated(EnumType.STRING)
    private RepeatType repeatType;

    @Column(name = "repeat_cycle")
    @Enumerated(EnumType.STRING)
    private RepeatCycle repeatCycle;

    @Column(name = "notice_yn")
    private boolean noticeYn;

    @Column(name = "notice_at")
    private long noticeAt;

    @Column(name = "priority")
    @Enumerated(EnumType.STRING)
    private PriorityType priority;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ScheduleStatus status;

    @OneToOne(mappedBy = "schedule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CustomRepeatPattern customRepeatPattern;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public ScheduleListResponse toListDto(List<PetSchedule> petScheduleList
            , List<Long> petIds, List<String> petNames) {
        Schedule scheduleEntity = this;

        return ScheduleListResponse.builder()
                .scheduleId(scheduleEntity.getScheduleId())
                .title(scheduleEntity.getScheduleTitle())
                .scheduleAt(scheduleEntity.getScheduleAt())
                .priority(scheduleEntity.getPriority())
                .status(scheduleEntity.getStatus())
                .petId(petIds)
                .petName(petNames)
                .build();
    }

    public ScheduleDetailResponse toDetailDto(
            List<Long> petIds, List<String> petNames
    ) {

        return ScheduleDetailResponse.builder()
                .categoryId(this.getScheduleCategory().getCategoryId())
                .categoryName(this.getScheduleCategory().getCategoryName())
                .scheduleId(this.getScheduleId())
                .title(this.getScheduleTitle())
                .content(this.getScheduleContent())
                .scheduleAt(this.getScheduleAt())
                .repeatType(this.getRepeatType())
                .repeatCycle(this.getRepeatCycle())
                .noticeYn(this.isNoticeYn())
                .noticeAt(this.getNoticeAt())
                .priority(this.getPriority())
                .status(this.getStatus())
                .petId(petIds)
                .petName(petNames)
                .build();
    }
}
