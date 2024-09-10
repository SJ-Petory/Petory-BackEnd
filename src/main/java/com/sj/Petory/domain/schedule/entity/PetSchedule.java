package com.sj.Petory.domain.schedule.entity;

import com.sj.Petory.domain.pet.entity.Pet;
import com.sj.Petory.domain.schedule.dto.ScheduleListResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
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

    public ScheduleListResponse toDto(List<PetSchedule> petScheduleList) {
        Schedule scheduleEntity = this.schedule;

        List<Long> petIds = petScheduleList.stream()
                .map(petSchedule -> petSchedule.getPet().getPetId())
                .toList();

        List<String> petNames = petScheduleList.stream()
                .map(petSchedule -> petSchedule.getPet().getPetName())
                .toList();

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
}

