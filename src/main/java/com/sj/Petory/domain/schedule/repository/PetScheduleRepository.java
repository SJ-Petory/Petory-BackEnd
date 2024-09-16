package com.sj.Petory.domain.schedule.repository;

import com.sj.Petory.domain.pet.entity.Pet;
import com.sj.Petory.domain.schedule.entity.PetSchedule;
import com.sj.Petory.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetScheduleRepository extends JpaRepository<PetSchedule, Long> {
    List<PetSchedule> findByPet(Pet pet);

    List<PetSchedule> findBySchedule(Schedule schedule);

    @Query("select ps.schedule.id from PetSchedule ps " +
            " where ps.pet.id = :petId ")
    List<Long> findScheduleIdByPet(@Param("petId") Long petId);
}
