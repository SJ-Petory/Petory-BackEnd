package com.sj.Petory.domain.schedule.entity;

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
}
