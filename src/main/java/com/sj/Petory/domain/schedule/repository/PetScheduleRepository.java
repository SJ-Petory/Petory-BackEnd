package com.sj.Petory.domain.schedule.repository;

import com.sj.Petory.domain.pet.entity.Pet;
import com.sj.Petory.domain.schedule.entity.PetSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetScheduleRepository extends JpaRepository<PetSchedule, Long> {
    List<PetSchedule> findByPet(Pet pet);
}
