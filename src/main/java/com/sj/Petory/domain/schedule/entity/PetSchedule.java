package com.sj.Petory.domain.schedule.entity;

import com.sj.Petory.domain.pet.entity.Pet;
import com.sj.Petory.domain.schedule.dto.ScheduleListResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "petschedule")
public class PetSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pet_schedule_id")
    private Long petScheduleId;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;
}

