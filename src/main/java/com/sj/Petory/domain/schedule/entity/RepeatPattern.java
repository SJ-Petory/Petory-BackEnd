package com.sj.Petory.domain.schedule.entity;

import com.sj.Petory.domain.schedule.dto.RepeatPatternDto;
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
@Table(name = "repeatpattern")
public class RepeatPattern {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "repeat_pattern_id")
    private Long repeatPatternId;

    @OneToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @Enumerated(EnumType.STRING)
    private Frequency frequency;

    @Column(name = "repeat_interval")
    private Long repeatInterval;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = DayOfWeek.class)
    @CollectionTable(name = "repeat_days_of_week", joinColumns = @JoinColumn(name = "repeat_pattern_id"))
    @Column(name = "day_of_week")
    private Set<DayOfWeek> daysOfWeek;

    @ElementCollection
    @CollectionTable(name = "repeat_days_of_month", joinColumns = @JoinColumn(name = "repeat_pattern_id"))
    @Column(name = "day_of_month")
    private Set<Integer> daysOfMonth;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    public RepeatPatternDto.Response toDto() {

        return RepeatPatternDto.Response.builder()
                .frequency(this.getFrequency())
                .interval(this.getRepeatInterval())
                .daysOfWeek(this.getDaysOfWeek())
                .daysOfMonth(this.getDaysOfMonth())
                .startDate(String.valueOf(this.getStartDate()))
                .endDate(String.valueOf(this.getEndDate()))
                .build();
    }
}
