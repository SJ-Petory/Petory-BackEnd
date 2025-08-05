package com.sj.Petory.domain.schedule.repository;

import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.schedule.entity.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    boolean existsByScheduleIdAndMember(Long scheduleId, Member member);

    List<Schedule> findByMember(Member member);

    @Query("select distinct cg.member.id from CareGiver cg" +
            " join PetSchedule ps on cg.pet = ps.pet" +
            " join Schedule s on ps.schedule = s" +
            " where s.scheduleId = :scheduleId")
    List<Long> findCareGiver(@Param("scheduleId") Long scheduleId);

    @Query("select s from Schedule s" +
            " join PetSchedule ps ON s.scheduleId = ps.schedule.scheduleId" +
            " join Pet p ON p.petId = ps.pet.petId" +
            " join CareGiver cg ON cg.pet.petId = p.petId" +
            " where cg.member.memberId = :memberId")
    List<Schedule> findByAllSchedule(@Param("memberId") Long memberId);

    @Query("select s from Schedule s" +
            " join PetSchedule ps on ps.schedule.scheduleId = s.scheduleId" +
            " join Pet p on p.petId = ps.pet.petId" +
            " where p.member.memberId = :memberId")
    List<Schedule> findByPetSchedule(@Param("memberId") Long memberId);
}
