package com.sj.Petory.domain.schedule.entity;

import com.sj.Petory.domain.schedule.type.Frequency;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "customrepeatpattern")
public class CustomRepeatPattern {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "custom_repeat_id")
    private Long customRepeatId;

    @OneToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @Enumerated(EnumType.STRING)
    private Frequency frequency;

    private Long repeatInterval;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = DayOfWeek.class)
    @CollectionTable(name = "custom_repeat_days_of_week", joinColumns = @JoinColumn(name = "custom_repeat_id"))
    @Column(name = "day_of_week")
    private Set<DayOfWeek> daysOfWeek;

    @ElementCollection
    @CollectionTable(name = "custom_repeat_days_of_month", joinColumns = @JoinColumn(name = "custom_repeat_id"))
    @Column(name = "day_of_month")
    private Set<Integer> daysOfMonth;

    @Column(name = "end_date")
    private LocalDateTime endDate;
}
