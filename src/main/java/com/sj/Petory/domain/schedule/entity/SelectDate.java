package com.sj.Petory.domain.schedule.entity;

import com.sj.Petory.domain.schedule.dto.DateInfo;
import com.sj.Petory.domain.schedule.type.ScheduleStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@Table(name = "selectdate")
public class SelectDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long select_id;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @Column(name = "selected_date")
    private LocalDateTime selectedDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ScheduleStatus status;

    public DateInfo toDateInfo() {

        return DateInfo.builder()
                .date(this.getSelectedDate())
                .status(this.getStatus())
                .build();
    }
}
