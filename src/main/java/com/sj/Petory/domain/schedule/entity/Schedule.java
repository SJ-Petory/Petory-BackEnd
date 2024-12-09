package com.sj.Petory.domain.schedule.entity;

import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.pet.entity.Pet;
import com.sj.Petory.domain.schedule.dto.ScheduleDetailResponse;
import com.sj.Petory.domain.schedule.dto.ScheduleListResponse;
import com.sj.Petory.domain.schedule.type.PriorityType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
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

    @Column(name = "is_all_day")
    private boolean isAllDay;

    @Column(name = "schedule_time")
    private LocalTime scheduleTime;

    @Column(name = "repeat_yn")
    private boolean repeatYn;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SelectDate> selectedDates;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PetSchedule> petSchedules;

    @Column(name = "notice_yn")
    private boolean noticeYn;

    @Column(name = "notice_at")
    private long noticeAt;

    @Column(name = "priority")
    @Enumerated(EnumType.STRING)
    private PriorityType priority;

    @OneToOne(mappedBy = "schedule", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private RepeatPattern repeatPattern;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;


    public ScheduleListResponse toListDto(List<Pet> petList) {

        return ScheduleListResponse.builder()
                .categoryId(this.getScheduleCategory().getCategoryId())
                .scheduleId(this.getScheduleId())
                .title(this.getScheduleTitle())
                .priority(this.getPriority())
                .petId(petList.stream().map(Pet::getPetId)
                        .collect(Collectors.toList()))
                .petName(petList.stream().map(Pet::getPetName)
                        .collect(Collectors.toList()))
                .dateInfo(this.getSelectedDates().stream()
                        .map(SelectDate::toDateInfo).toList())
                .build();
    }

    public ScheduleDetailResponse toDetailDto(List<Pet> petList
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
                .isAllDay(this.isAllDay())
                .repeatYn(this.isRepeatYn())
                .petId(petList.stream().map(Pet::getPetId)
                        .collect(Collectors.toList()))
                .petName(petList.stream().map(Pet::getPetName)
                        .collect(Collectors.toList()))
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
