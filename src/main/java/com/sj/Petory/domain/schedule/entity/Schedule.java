package com.sj.Petory.domain.schedule.entity;

import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.schedule.dto.ScheduleDetailResponse;
import com.sj.Petory.domain.schedule.dto.ScheduleListResponse;
import com.sj.Petory.domain.schedule.dto.ScheduleUpdateRequest;
import com.sj.Petory.domain.schedule.type.PriorityType;
import com.sj.Petory.domain.schedule.type.ScheduleStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@DynamicUpdate
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

    @Column(name = "repeat_yn")
    private boolean repeatYn;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SelectDate> selectedDates;

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

    @OneToOne(mappedBy = "schedule", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private RepeatPattern repeatPattern;

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
                .noticeYn(this.isNoticeYn())
                .noticeAt(this.getNoticeAt())
                .priority(this.getPriority())
                .status(this.getStatus())
                .petId(petIds)
                .petName(petNames)
                .build();
    }

//    public void updateSchedule(ScheduleCategory category, ScheduleUpdateRequest request) {
//        if (!ObjectUtils.isEmpty(category)) {
//            this.scheduleCategory = category;
//        }
//        if (StringUtils.hasText(request.getTitle())) {
//            this.scheduleTitle = request.getTitle();
//        }
//        if (StringUtils.hasText(request.getContent())) {
//            this.scheduleContent = request.getContent();
//        }
//        if (!ObjectUtils.isEmpty(request.getScheduleAt())) {
//            this.scheduleAt = request.getScheduleAt();
//        }
//        if (StringUtils.hasText(String.valueOf(request.isRepeatYn()))) {
//            this.repeatYn = request.isRepeatYn();
//        }
//
//        if (!ObjectUtils.isEmpty(request.isNoticeYn())) {
//            this.noticeYn = request.isNoticeYn();
//        }
//        if (!ObjectUtils.isEmpty(request.getNoticeAt())) {
//            this.noticeAt = request.getNoticeAt();
//        }
//        if (StringUtils.hasText(String.valueOf(request.getPriority()))) {
//            this.priority = request.getPriority();
//        }
//    }
}
